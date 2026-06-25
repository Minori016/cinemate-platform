package com.cinema.cinemate.controller.admin;

import com.cinema.cinemate.entity.Movie;
import com.cinema.cinemate.entity.Showtime;
import com.cinema.cinemate.repository.MovieRepository;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.repository.ShowtimeRepository;
import com.cinema.cinemate.request.ManualShowtimeCreateRequest;
import com.cinema.cinemate.response.ShowtimeValidationResponse;
import com.cinema.cinemate.service.ShowtimeValidationService;
import com.cinema.cinemate.request.AutoGenerateRequest;
import com.cinema.cinemate.response.ShowtimePreviewResponse;
import com.cinema.cinemate.response.ShowtimeResponse;
import com.cinema.cinemate.service.ShowtimeAutoGenerateService;
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
    private final ShowtimeAutoGenerateService autoGenerateService;
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

    @PostMapping("/auto-generate")
    public ResponseEntity<List<ShowtimePreviewResponse>> autoGenerateShowtimes(
            @Valid @RequestBody AutoGenerateRequest request) {
        List<ShowtimePreviewResponse> previewList = autoGenerateService.generate(request);
        return ResponseEntity.ok(previewList);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createBatchShowtimes(
            @RequestBody List<ShowtimePreviewResponse> requestList) {
        
        List<Showtime> toSave = new java.util.ArrayList<>();
        for (ShowtimePreviewResponse req : requestList) {
            ManualShowtimeCreateRequest manualReq = ManualShowtimeCreateRequest.builder()
                .movieId(req.getMovieId())
                .roomId(req.getRoomId())
                .startTime(req.getStartTime())
                .format(req.getFormat())
                .language(req.getLanguage())
                .basePrice(req.getBasePrice())
                .build();
            
            try {
                Showtime showtime = showtimeService.createShowtime(manualReq);
                toSave.add(showtime);
            } catch (Exception e) {
                // Skip if movie or room not found
            }
        }
        
        return ResponseEntity.ok(toSave);
    }
}
