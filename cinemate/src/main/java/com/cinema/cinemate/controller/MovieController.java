package com.cinema.cinemate.controller;

import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.MovieActorResponse;
import com.cinema.cinemate.response.MovieResponse;
import com.cinema.cinemate.response.PageResponse;
import com.cinema.cinemate.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> getMovies(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "genreId", required = false) UUID genreId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .result(movieService.getMovies(search, genreId, status, page, size))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getMovieById(@PathVariable UUID id) {
        return ApiResponse.<MovieResponse>builder()
                .result(movieService.getMovieById(id))
                .build();
    }

    @GetMapping("/{id}/actors")
    public ApiResponse<List<MovieActorResponse>> getMovieActors(@PathVariable UUID id) {
        return ApiResponse.<List<MovieActorResponse>>builder()
                .result(movieService.getMovieActors(id))
                .build();
    }
}
