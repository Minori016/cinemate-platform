package com.cinema.cinemate.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequest {

    @NotBlank(message = "Movie name (Vietnamese) is required")
    private String titleVn;

    private String titleEn;

    @NotBlank(message = "Movie description is required")
    private String description;

    @NotBlank(message = "Director is required")
    private String director;

    @NotNull(message = "Running time is required")
    @Min(value = 1, message = "Running time must be at least 1 minute")
    private Integer durationMinutes;

    private String rating;

    @NotBlank(message = "Version (2D/3D) is required")
    private String version;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    private String language;

    private String trailerUrl;

    @NotEmpty(message = "At least one genre must be selected")
    private Set<UUID> genreIds;

    private Set<UUID> countryIds;

    @Valid
    private List<ActorInput> actors;

    // Showtimes are kept in the request to match the frontend shape as per user request, 
    // but they will not be processed during update to avoid data corruption.
    @Valid
    private List<ShowtimeInput> showtimes;
}
