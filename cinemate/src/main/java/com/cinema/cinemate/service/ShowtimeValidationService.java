package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.Movie;
import com.cinema.cinemate.entity.CinemaRoom;
import com.cinema.cinemate.repository.MovieRepository;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.repository.ShowtimeRepository;
import com.cinema.cinemate.request.ManualShowtimeCreateRequest;
import com.cinema.cinemate.response.ShowtimeValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import com.cinema.cinemate.enums.ShowtimeFormat;
import com.cinema.cinemate.enums.ShowtimeLanguage;

@Service
@RequiredArgsConstructor
public class ShowtimeValidationService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CinemaRoomRepository cinemaRoomRepository;

    private static final long CLEANING_TIME_MINUTES = 15;

    public ShowtimeValidationResponse validateManualCreation(ManualShowtimeCreateRequest req) {
        List<String> hardErrors = new ArrayList<>();
        List<String> softWarnings = new ArrayList<>();

        // ===== Check thời gian trong quá khứ =====
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        if (req.getStartTime().isBefore(now)) {
            hardErrors.add("Không thể tạo suất chiếu trong quá khứ. Thời gian bắt đầu phải sau thời điểm hiện tại (" + 
                now.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + 
                " - Giờ Việt Nam).");
        }

        // ===== Validate format bằng enum =====
        if (req.getFormat() != null) {
            ShowtimeFormat format = ShowtimeFormat.fromString(req.getFormat());
            if (format == null) {
                hardErrors.add("Định dạng chiếu '" + req.getFormat() + "' không hợp lệ. " +
                    "Chấp nhận: 2D, 3D, IMAX, 4DX");
            }
        }

        // ===== Validate language bằng enum =====
        if (req.getLanguage() != null) {
            ShowtimeLanguage language = ShowtimeLanguage.fromString(req.getLanguage());
            if (language == null) {
                hardErrors.add("Ngôn ngữ chiếu '" + req.getLanguage() + "' không hợp lệ. " +
                    "Chấp nhận: Phụ đề, Lồng tiếng, Subtitled, Dubbed");
            }
        }

        Movie movie = movieRepository.findById(req.getMovieId()).orElse(null);
        CinemaRoom room = cinemaRoomRepository.findById(req.getRoomId()).orElse(null);

        if (movie == null) {
            hardErrors.add("Không tìm thấy phim có ID: " + req.getMovieId());
        }
        if (room == null) {
            hardErrors.add("Không tìm thấy phòng chiếu có ID: " + req.getRoomId());
        }

        if (movie != null && room != null) {
            // 1. Hard Constraints: Overlap checking
            OffsetDateTime startTime = req.getStartTime();
            OffsetDateTime endTime = startTime.plusMinutes(movie.getDurationMinutes());

            boolean hasOverlap = showtimeRepository.hasOverlapWithCleaning(
                    room.getId(),
                    startTime.minusMinutes(CLEANING_TIME_MINUTES),
                    endTime.plusMinutes(CLEANING_TIME_MINUTES)
            );

            if (hasOverlap) {
                hardErrors.add(String.format("Phòng chiếu %s đã có lịch chiếu trùng lấp thời gian. (Cần đảm bảo khoảng cách %d phút dọn rạp).", 
                        room.getName(), CLEANING_TIME_MINUTES));
            }

            // 2. Soft Warnings: Logic based on genre and time
            LocalTime time = startTime.toLocalTime();
            
            boolean isHorror = movie.getGenres().stream()
                    .anyMatch(g -> g.getName().toLowerCase().contains("kinh dị") || g.getName().toLowerCase().contains("giật gân"));
            
            if (isHorror && time.isBefore(LocalTime.of(12, 0))) {
                softWarnings.add("Phim Kinh dị / Giật gân thường không phù hợp chiếu vào buổi sáng. Bạn có chắc chắn muốn tiếp tục?");
            }

            boolean isFamily = movie.getGenres().stream()
                    .anyMatch(g -> g.getName().toLowerCase().contains("hoạt hình") || g.getName().toLowerCase().contains("gia đình"));
            
            if (isFamily && time.isAfter(LocalTime.of(22, 0))) {
                softWarnings.add("Phim Hoạt hình / Gia đình thường ít khách vào khung giờ tối muộn (sau 22:00). Bạn có chắc chắn muốn tiếp tục?");
            }

            if ("C18".equalsIgnoreCase(movie.getRating()) && time.isBefore(LocalTime.of(12, 0))) {
                softWarnings.add("Phim gắn nhãn C18 đang được xếp vào buổi sáng. Bạn có chắc chắn muốn tiếp tục?");
            }
            
            // Cảnh báo nếu chọn giờ vàng cho phim không nổi bật (có thể mở rộng thêm logic)
        }

        return ShowtimeValidationResponse.builder()
                .valid(hardErrors.isEmpty())
                .hardErrors(hardErrors)
                .softWarnings(softWarnings)
                .build();
    }
}
