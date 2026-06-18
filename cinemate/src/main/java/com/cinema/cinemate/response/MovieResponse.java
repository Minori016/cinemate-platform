package com.cinema.cinemate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private UUID id;
    private String titleVn;
    private String titleEn;
    private String description;
    private String director;
    private Integer durationMinutes;
    private String rating;
    private String version;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String language;
    private String posterUrl;
    private String trailerUrl;
    private Set<GenreResponse> genres;
    private Set<CountryResponse> countries;
    private Set<MovieMediaResponse> media;
    private Set<MovieActorResponse> actors;
    private Set<ShowtimeResponse> showtimes;
}
