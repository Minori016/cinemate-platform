package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.CinemaRoom;
import com.cinema.cinemate.entity.Movie;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.repository.MovieRepository;
import com.cinema.cinemate.repository.ShowtimeRepository;
import com.cinema.cinemate.request.AutoGenerateRequest;
import com.cinema.cinemate.response.ShowtimePreviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowtimeAutoGenerateService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CinemaRoomRepository cinemaRoomRepository;

    private static final long CLEANING_TIME_MINUTES = 15;

    public List<ShowtimePreviewResponse> generate(AutoGenerateRequest req) {
        List<ShowtimePreviewResponse> previewList = new ArrayList<>();
        
        List<UUID> movieUuids = new ArrayList<>();
        if (req.getMovieIds() != null) {
            for (String id : req.getMovieIds()) {
                try { movieUuids.add(UUID.fromString(id)); } catch (Exception e) {}
            }
        }
        
        List<UUID> roomUuids = new ArrayList<>();
        if (req.getRoomIds() != null) {
            for (String id : req.getRoomIds()) {
                try { roomUuids.add(UUID.fromString(id)); } catch (Exception e) {}
            }
        }

        List<Movie> selectedMovies = movieRepository.findAllById(movieUuids);
        List<CinemaRoom> rooms = cinemaRoomRepository.findAllById(roomUuids);
        
        // Map để lưu "Điểm phạt (Penalty)" của từng phim
        Map<UUID, Double> penaltyMap = new HashMap<>();
        selectedMovies.forEach(m -> penaltyMap.put(m.getId(), 0.0));

        LocalDate currentDate = req.getStartDate();
        while (!currentDate.isAfter(req.getEndDate())) {
            
            for (CinemaRoom room : rooms) {
                // Khởi tạo con trỏ thời gian (Time Pointer) tại giờ mở cửa (Giả sử múi giờ UTC hoặc mặc định server)
                OffsetDateTime timePointer = OffsetDateTime.of(currentDate, req.getOpenTime(), ZoneOffset.UTC);
                OffsetDateTime closeTime = OffsetDateTime.of(currentDate, req.getCloseTime(), ZoneOffset.UTC);

                while (timePointer.isBefore(closeTime)) {
                    Movie bestMovie = null;
                    double bestScore = -999.0;

                    for (Movie movie : selectedMovies) {
                        double score = calculatePriorityScore(movie, timePointer, currentDate, req);
                        score -= penaltyMap.getOrDefault(movie.getId(), 0.0); // Trừ điểm phạt

                        if (score > bestScore) {
                            bestScore = score;
                            bestMovie = movie;
                        }
                    }

                    if (bestMovie == null) break;

                    OffsetDateTime endTime = timePointer.plusMinutes(bestMovie.getDurationMinutes());
                    
                    // Nếu thời gian kết thúc vượt quá giờ đóng cửa -> Bỏ qua
                    if (endTime.isAfter(closeTime)) {
                        timePointer = timePointer.plusMinutes(30); // Tịnh tiến 30 phút thử lại
                        continue;
                    }

                    // Kiểm tra Overlap (Conflict Check) bao gồm dọn rạp
                    boolean hasOverlap = showtimeRepository.hasOverlapWithCleaning(
                            room.getId(),
                            timePointer.minusMinutes(CLEANING_TIME_MINUTES),
                            endTime.plusMinutes(CLEANING_TIME_MINUTES)
                    );

                    if (!hasOverlap) {
                        // Pass qua các vòng check -> Thêm vào danh sách ảo
                        boolean isGoldenHour = !timePointer.toLocalTime().isBefore(req.getGoldenHourStart()) 
                                            && !timePointer.toLocalTime().isAfter(req.getGoldenHourEnd());

                        previewList.add(ShowtimePreviewResponse.builder()
                                .tempId(UUID.randomUUID())
                                .movieId(bestMovie.getId())
                                .movieTitle(bestMovie.getTitleVn())
                                .roomId(room.getId())
                                .roomName(room.getName())
                                .startTime(timePointer)
                                .endTime(endTime)
                                .priorityScore(bestScore)
                                .format(req.getFormat())
                                .language(req.getLanguage())
                                .isGoldenHour(isGoldenHour)
                                .basePrice(isGoldenHour 
                                    ? req.getBasePrice().add(new java.math.BigDecimal("15000")) 
                                    : req.getBasePrice())
                                .build());

                        // Tăng điểm phạt cho phim này để cân bằng ở khung giờ tiếp theo
                        penaltyMap.put(bestMovie.getId(), penaltyMap.getOrDefault(bestMovie.getId(), 0.0) + 20.0);

                        // Tịnh tiến Time Pointer qua hẳn phim và thời gian dọn rạp
                        timePointer = endTime.plusMinutes(CLEANING_TIME_MINUTES);
                    } else {
                        // Bị vướng lịch cũ trong DB, trượt đi 30 phút
                        timePointer = timePointer.plusMinutes(30);
                    }
                }
                // Hết 1 ngày cho 1 phòng, reset Penalty
                selectedMovies.forEach(m -> penaltyMap.put(m.getId(), 0.0));
            }
            currentDate = currentDate.plusDays(1);
        }

        return previewList;
    }

    private double calculatePriorityScore(Movie movie, OffsetDateTime time, LocalDate date, AutoGenerateRequest req) {
        double score = 50.0; // Base score

        // 1. Ngày trong tuần (Day of week)
        DayOfWeek day = date.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        if (isWeekend) score += 20;

        // 2. Thời gian chiếu (Giờ vàng)
        LocalTime lt = time.toLocalTime();
        boolean isGoldenHour = !lt.isBefore(req.getGoldenHourStart()) && !lt.isAfter(req.getGoldenHourEnd());
        if (isGoldenHour) score += 40;

        // 3. Đánh giá (Rating / Độ Hot - Ví dụ C18 = Khuya)
        if ("C18".equalsIgnoreCase(movie.getRating())) {
            if (lt.isBefore(LocalTime.of(12, 0))) score -= 30; // Phạt nếu chiếu sáng
            if (lt.isAfter(LocalTime.of(20, 0))) score += 25;  // Thưởng chiếu muộn
        }

        // 4. Thể loại (Genre)
        boolean isAnimationOrFamily = movie.getGenres().stream()
                .anyMatch(g -> g.getName().toLowerCase().contains("hoạt hình") || g.getName().toLowerCase().contains("gia đình"));
        if (isAnimationOrFamily) {
            if (lt.isBefore(LocalTime.of(17, 0))) score += 30; // Trẻ em đi xem sáng/chiều
            if (lt.isAfter(LocalTime.of(21, 0))) score -= 50;  // Trẻ em đi ngủ
        }

        boolean isHorror = movie.getGenres().stream()
                .anyMatch(g -> g.getName().toLowerCase().contains("kinh dị") || g.getName().toLowerCase().contains("giật gân"));
        if (isHorror) {
            if (lt.isAfter(LocalTime.of(19, 0))) score += 35; // Khuya hợp kinh dị
        }

        return score;
    }
}
