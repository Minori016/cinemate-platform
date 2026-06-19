package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.*;
import com.cinema.cinemate.enums.ErrorCode;
import com.cinema.cinemate.exception.AppException;
import com.cinema.cinemate.repository.*;
import com.cinema.cinemate.request.AddMovieRequest;
import com.cinema.cinemate.request.ActorInput;
import com.cinema.cinemate.request.ShowtimeInput;
import com.cinema.cinemate.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final CountryRepository countryRepository;
    private final ActorRepository actorRepository;
    private final CinemaRoomRepository cinemaRoomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final CloudinaryService cloudinaryService;

    // ==========================================
    // PUBLIC READ OPERATIONS (existing)
    // ==========================================

    public PageResponse<MovieResponse> getMovies(String search, UUID genreId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("titleVn").ascending());
        LocalDate today = LocalDate.now();

        Page<Movie> moviePage = movieRepository.findMoviesWithFilters(
                search != null && !search.trim().isEmpty() ? search.trim() : null,
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

    // ==========================================
    // ADMIN: ADD MOVIE (AC-01 → AC-04)
    // ==========================================

    @Transactional
    public MovieResponse addMovie(AddMovieRequest request, MultipartFile posterFile) {
        // 1. Validate business rules
        validateAddMovieRequest(request);

        // 2. Upload poster to Cloudinary (if provided)
        String posterUrl = uploadPoster(posterFile);

        // 3. Resolve genres
        Set<Genre> genres = resolveGenres(request.getGenreIds());

        // 4. Resolve countries
        Set<Country> countries = resolveCountries(request.getCountryIds());

        // 5. Build Movie entity
        Movie movie = Movie.builder()
                .titleVn(request.getTitleVn().trim())
                .titleEn(request.getTitleEn() != null ? request.getTitleEn().trim() : null)
                .description(request.getDescription().trim())
                .director(request.getDirector().trim())
                .durationMinutes(request.getDurationMinutes())
                .rating(request.getRating())
                .version(request.getVersion().trim())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .language(request.getLanguage())
                .posterUrl(posterUrl)
                .trailerUrl(request.getTrailerUrl())
                .genres(genres)
                .countries(countries)
                .build();

        // 6. Save movie first to get ID
        Movie savedMovie = movieRepository.save(movie);

        // 7. Resolve/Create actors and link to movie
        if (request.getActors() != null && !request.getActors().isEmpty()) {
            for (ActorInput actorInput : request.getActors()) {
                Actor actor = actorRepository.findByFullName(actorInput.getFullName().trim())
                        .orElseGet(() -> actorRepository.save(
                                Actor.builder()
                                        .fullName(actorInput.getFullName().trim())
                                        .build()
                        ));

                MovieActor movieActor = MovieActor.builder()
                        .id(new MovieActorId(savedMovie.getId(), actor.getId()))
                        .movie(savedMovie)
                        .actor(actor)
                        .characterName(actorInput.getCharacterName())
                        .build();

                savedMovie.getMovieActors().add(movieActor);
            }
        }

        // 8. Create showtimes (AC-02)
        List<Showtime> createdShowtimes = new ArrayList<>();
        if (request.getShowtimes() != null && !request.getShowtimes().isEmpty()) {
            for (ShowtimeInput showtimeInput : request.getShowtimes()) {
                Showtime showtime = createShowtime(savedMovie, showtimeInput);
                createdShowtimes.add(showtime);
            }
        }

        // 9. Save movie with actors
        savedMovie = movieRepository.save(savedMovie);

        log.info("MOVIE_ADDED | id={} | title={}", savedMovie.getId(), savedMovie.getTitleVn());

        return toMovieResponseWithShowtimes(savedMovie, createdShowtimes);
    }

    // ==========================================
    // ADMIN: DELETE MOVIE
    // ==========================================

    @Transactional
    public void deleteMovie(UUID movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        // Delete associated showtimes first (no cascade from Movie side)
        List<Showtime> showtimes = showtimeRepository.findByMovieId(movieId);
        if (!showtimes.isEmpty()) {
            showtimeRepository.deleteAll(showtimes);
        }

        // MovieActors and MovieMedia are auto-deleted via CascadeType.ALL + orphanRemoval
        // ManyToMany join tables (movie_genres, movie_countries) are handled by JPA
        movieRepository.delete(movie);

        log.info("MOVIE_DELETED | id={} | title={}", movieId, movie.getTitleVn());
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private void validateAddMovieRequest(AddMovieRequest request) {
        // Check duplicate title
        if (movieRepository.existsByTitleVn(request.getTitleVn().trim())) {
            throw new AppException(ErrorCode.MOVIE_ALREADY_EXISTS);
        }

        // Check date range
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private String uploadPoster(MultipartFile posterFile) {
        if (posterFile == null || posterFile.isEmpty()) {
            return null;
        }
        try {
            return cloudinaryService.uploadFile(posterFile);
        } catch (IOException e) {
            log.error("Failed to upload poster: ", e);
            throw new AppException(ErrorCode.POSTER_UPLOAD_FAILED);
        }
    }

    private Set<Genre> resolveGenres(Set<UUID> genreIds) {
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));
        if (genres.size() != genreIds.size()) {
            throw new AppException(ErrorCode.GENRE_NOT_FOUND);
        }
        return genres;
    }

    private Set<Country> resolveCountries(Set<UUID> countryIds) {
        if (countryIds == null || countryIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Country> countries = new HashSet<>(countryRepository.findAllById(countryIds));
        if (countries.size() != countryIds.size()) {
            throw new AppException(ErrorCode.COUNTRY_NOT_FOUND);
        }
        return countries;
    }

    private Showtime createShowtime(Movie movie, ShowtimeInput input) {
        // Validate cinema room exists
        CinemaRoom room = cinemaRoomRepository.findById(input.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_ROOM_NOT_FOUND));

        // Calculate end time based on movie duration
        var endTime = input.getStartTime().plusMinutes(movie.getDurationMinutes());

        // Check for schedule conflict in the same room
        boolean hasConflict = showtimeRepository
                .existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        input.getRoomId(), endTime, input.getStartTime());

        if (hasConflict) {
            throw new AppException(ErrorCode.SHOWTIME_CONFLICT);
        }

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(input.getStartTime())
                .endTime(endTime)
                .status("SCHEDULED")
                .build();

        return showtimeRepository.save(showtime);
    }

    // ==========================================
    // MAPPERS
    // ==========================================

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
                .actors(movie.getMovieActors().stream()
                        .map(this::toMovieActorResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    private MovieResponse toMovieResponseWithShowtimes(Movie movie, List<Showtime> showtimes) {
        MovieResponse response = toMovieResponse(movie);

        if (showtimes != null && !showtimes.isEmpty()) {
            response.setShowtimes(showtimes.stream()
                    .map(s -> ShowtimeResponse.builder()
                            .id(s.getId())
                            .roomId(s.getRoom().getId())
                            .roomName(s.getRoom().getName())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .status(s.getStatus())
                            .build())
                    .collect(Collectors.toSet()));
        }

        return response;
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
