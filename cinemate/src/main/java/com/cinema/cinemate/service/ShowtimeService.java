package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.CinemaRoom;
import com.cinema.cinemate.entity.Movie;
import com.cinema.cinemate.entity.Showtime;
import com.cinema.cinemate.entity.ShowtimePrice;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.repository.MovieRepository;
import com.cinema.cinemate.repository.ShowtimeRepository;
import com.cinema.cinemate.request.ManualShowtimeCreateRequest;
import com.cinema.cinemate.response.ShowtimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CinemaRoomRepository cinemaRoomRepository;

    @Transactional(readOnly = true)
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream().map(s -> {
            BigDecimal basePrice = BigDecimal.valueOf(90000);
            if (s.getPrices() != null && !s.getPrices().isEmpty()) {
                // Find standard price, otherwise take first
                basePrice = s.getPrices().stream()
                        .filter(p -> "Standard".equalsIgnoreCase(p.getSeatType()))
                        .map(ShowtimePrice::getPrice)
                        .findFirst()
                        .orElse(s.getPrices().get(0).getPrice());
            }

            return ShowtimeResponse.builder()
                    .id(s.getId())
                    .movieId(s.getMovie() != null ? s.getMovie().getId() : null)
                    .movieTitle(s.getMovie() != null ? s.getMovie().getTitleVn() : "Unknown Movie")
                    .roomId(s.getRoom() != null ? s.getRoom().getId() : null)
                    .roomName(s.getRoom() != null ? s.getRoom().getName() : "Unknown Room")
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .format(s.getFormat())
                    .language(s.getLanguage())
                    .basePrice(basePrice)
                    .status(s.getStatus())
                    .build();
        }).toList();
    }

    @Transactional
    public Showtime createShowtime(ManualShowtimeCreateRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        CinemaRoom room = cinemaRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(request.getStartTime())
                .endTime(request.getStartTime().plusMinutes(movie.getDurationMinutes()))
                .format(request.getFormat())
                .language(request.getLanguage())
                .status("SCHEDULED")
                .prices(new ArrayList<>())
                .build();

        // Cấu hình Base Price
        BigDecimal basePrice = request.getBasePrice();
        
        // Sinh giá cho ghế Thường (Standard) = Base Price
        ShowtimePrice standardPrice = ShowtimePrice.builder()
                .showtime(showtime)
                .seatType("Standard")
                .price(basePrice)
                .build();
        showtime.getPrices().add(standardPrice);

        // Sinh giá cho ghế VIP = Base Price + 20,000
        ShowtimePrice vipPrice = ShowtimePrice.builder()
                .showtime(showtime)
                .seatType("VIP")
                .price(basePrice.add(new BigDecimal("20000")))
                .build();
        showtime.getPrices().add(vipPrice);

        // Sinh giá cho ghế Đôi (Couple) = Base Price * 2 + 10,000
        ShowtimePrice couplePrice = ShowtimePrice.builder()
                .showtime(showtime)
                .seatType("Couple")
                .price(basePrice.multiply(new BigDecimal("2")).add(new BigDecimal("10000")))
                .build();
        showtime.getPrices().add(couplePrice);

        return showtimeRepository.save(showtime);
    }
}
