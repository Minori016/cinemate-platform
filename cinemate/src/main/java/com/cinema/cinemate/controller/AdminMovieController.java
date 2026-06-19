package com.cinema.cinemate.controller;

import com.cinema.cinemate.request.AddMovieRequest;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.MovieResponse;
import com.cinema.cinemate.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/movies")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AdminMovieController {

    private final MovieService movieService;

    /**
     * AC-01 → AC-05: Add a new movie with full information.
     *
     * Accepts multipart/form-data with:
     *   - "movie": JSON body (AddMovieRequest)
     *   - "posterFile": Image file (optional)
     *
     * On success (AC-03): Returns the created movie.
     * On validation failure (AC-04): Returns error response.
     * Unauthorized access (AC-05): Returns 401/403.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MovieResponse> addMovie(
            @RequestPart("movie") @Valid AddMovieRequest request,
            @RequestPart(value = "posterFile", required = false) MultipartFile posterFile
    ) {
        return ApiResponse.<MovieResponse>builder()
                .message("Movie added successfully")
                .result(movieService.addMovie(request, posterFile))
                .build();
    }

    /**
     * Delete a movie permanently from the system.
     *
     * AC-01: Frontend must show confirmation dialog before calling this endpoint.
     * AC-02: On success, movie is permanently deleted; returns success message.
     * AC-04: Only ADMIN role is authorized to perform this action.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
        return ApiResponse.<Void>builder()
                .message("Movie deleted successfully")
                .build();
    }
}
