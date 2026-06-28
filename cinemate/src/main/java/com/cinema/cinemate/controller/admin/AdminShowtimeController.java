package com.cinema.cinemate.controller.admin;

import com.cinema.cinemate.entity.Movie;
import com.cinema.cinemate.entity.Showtime;
import com.cinema.cinemate.repository.MovieRepository;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.repository.ShowtimeRepository;
import com.cinema.cinemate.request.ManualShowtimeCreateRequest;
import com.cinema.cinemate.response.ShowtimeValidationResponse;
import com.cinema.cinemate.service.ShowtimeValidationService;
import com.cinema.cinemate.response.ShowtimeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/showtimes")
@RequiredArgsConstructor
public class AdminShowtimeController {

    private final ShowtimeValidationService validationService;
    private final com.cinema.cinemate.service.ShowtimeService showtimeService;
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CinemaRoomRepository cinemaRoomRepository;

    @GetMapping
    public ResponseEntity<List<ShowtimeResponse>> getAllShowtimes() {
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @PostMapping("/validate")
    public ResponseEntity<ShowtimeValidationResponse> validateManualShowtime(
            @Valid @RequestBody ManualShowtimeCreateRequest request) {
        ShowtimeValidationResponse response = validationService.validateManualCreation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createManualShowtime(
            @Valid @RequestBody ManualShowtimeCreateRequest request,
            @RequestParam(defaultValue = "false") boolean ignoreWarnings) {
        
        // 1. Validate first
        ShowtimeValidationResponse validation = validationService.validateManualCreation(request);
        
        if (!validation.isValid()) {
            return ResponseEntity.badRequest().body(validation);
        }
        
        if (!validation.getSoftWarnings().isEmpty() && !ignoreWarnings) {
            // Require explicit confirmation
            return ResponseEntity.badRequest().body(validation);
        }

        // 2. Create Showtime via Service
        Showtime showtime = showtimeService.createShowtime(request);
        
        return ResponseEntity.ok(showtime);
    }
}
