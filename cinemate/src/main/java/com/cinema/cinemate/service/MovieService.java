package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.*;
import com.cinema.cinemate.enums.ErrorCode;
import com.cinema.cinemate.exception.AppException;
import com.cinema.cinemate.repository.MovieRepository;
import com.cinema.cinemate.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    public PageResponse<MovieResponse> getMovies(String search, UUID genreId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        LocalDate today = LocalDate.now();

        Page<Movie> moviePage = movieRepository.findMoviesWithFilters(
                search != null ? search.trim() : null,
                genreId,
                status != null ? status.trim().toLowerCase() : null,
                today,
                pageable
        );

        List<MovieResponse> content = moviePage.getContent().stream()
                .map(this::toMovieResponse)
                .collect(Collectors.toList());

        return PageResponse.<MovieResponse>builder()
                .content(content)
                .pageNumber(moviePage.getNumber())
                .pageSize(moviePage.getSize())
                .totalElements(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .last(moviePage.isLast())
                .build();
    }

    public MovieResponse getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        return toMovieResponse(movie);
    }

    public List<MovieActorResponse> getMovieActors(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        return movie.getMovieActors().stream()
                .map(this::toMovieActorResponse)
                .collect(Collectors.toList());
    }

    private MovieResponse toMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .titleVn(movie.getTitleVn())
                .titleEn(movie.getTitleEn())
                .description(movie.getDescription())
                .director(movie.getDirector())
                .durationMinutes(movie.getDurationMinutes())
                .rating(movie.getRating())
                .version(movie.getVersion())
                .fromDate(movie.getFromDate())
                .toDate(movie.getToDate())
                .language(movie.getLanguage())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .genres(movie.getGenres().stream()
                        .map(g -> GenreResponse.builder().id(g.getId()).name(g.getName()).build())
                        .collect(Collectors.toSet()))
                .countries(movie.getCountries().stream()
                        .map(c -> CountryResponse.builder().id(c.getId()).code(c.getCode()).name(c.getName()).build())
                        .collect(Collectors.toSet()))
                .media(movie.getMovieMedia().stream()
                        .map(m -> MovieMediaResponse.builder().id(m.getId()).mediaType(m.getMediaType()).url(m.getUrl()).title(m.getTitle()).build())
                        .collect(Collectors.toSet()))
                .build();
    }

    private MovieActorResponse toMovieActorResponse(MovieActor movieActor) {
        Actor actor = movieActor.getActor();
        return MovieActorResponse.builder()
                .actorId(actor.getId())
                .fullName(actor.getFullName())
                .avatarUrl(actor.getAvatarUrl())
                .characterName(movieActor.getCharacterName())
                .build();
    }
}
