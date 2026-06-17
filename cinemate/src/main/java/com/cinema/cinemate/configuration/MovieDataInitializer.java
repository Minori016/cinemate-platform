package com.cinema.cinemate.configuration;

import com.cinema.cinemate.entity.Actor;
import com.cinema.cinemate.entity.Genre;
import com.cinema.cinemate.entity.Movie;
import com.cinema.cinemate.entity.MovieActor;
import com.cinema.cinemate.repository.ActorRepository;
import com.cinema.cinemate.repository.GenreRepository;
import com.cinema.cinemate.repository.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class MovieDataInitializer implements ApplicationRunner {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Checking if movie data needs to be seeded...");

        try {
            ClassPathResource resource = new ClassPathResource("scraped_movies.json");
            if (!resource.exists()) {
                log.warn("scraped_movies.json not found in classpath. Skipping movie data seeding.");
                return;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                List<MovieJsonDto> movieDtos = objectMapper.readValue(inputStream, new TypeReference<List<MovieJsonDto>>() {});

                for (MovieJsonDto dto : movieDtos) {
                    if (movieRepository.existsByTitleVn(dto.getTitle())) {
                        log.info("Movie '{}' already exists, skipping.", dto.getTitle());
                        continue;
                    }

                    log.info("Seeding movie: {}", dto.getTitle());

                    LocalDate fromDate;
                    try {
                        fromDate = LocalDate.parse(dto.getRelease_date());
                    } catch (Exception e) {
                        fromDate = LocalDate.now();
                        log.warn("Invalid release date for movie '{}': '{}'. Defaulting to now.", dto.getTitle(), dto.getRelease_date());
                    }
                    LocalDate toDate = fromDate.plusMonths(2); // Default showtime window of 2 months

                    String actorsStr = dto.getActors() != null ? String.join(", ", dto.getActors()) : "";
                    String versionStr = dto.getFormat() != null ? String.join(", ", dto.getFormat()) : "2D";
                    if (versionStr != null && versionStr.length() > 20) {
                        versionStr = versionStr.substring(0, 20);
                    }

                    String languageStr = dto.getLanguage();
                    if (languageStr != null && languageStr.length() > 50) {
                        languageStr = languageStr.substring(0, 50);
                    }

                    // Process genres
                    Set<Genre> movieGenres = new HashSet<>();
                    if (dto.getGenre() != null) {
                        for (String genreName : dto.getGenre()) {
                            Genre genre = genreRepository.findByName(genreName)
                                    .orElseGet(() -> genreRepository.save(
                                            Genre.builder()
                                                    .name(genreName)
                                                    .build()
                                    ));
                            movieGenres.add(genre);
                        }
                    }

                    Movie movie = Movie.builder()
                            .titleVn(dto.getTitle())
                            .description(dto.getSynopsis())
                            .director(dto.getDirector())
                            .durationMinutes(dto.getDuration() > 0 ? dto.getDuration() : 120)
                            .version(versionStr)
                            .fromDate(fromDate)
                            .toDate(toDate)
                            .language(languageStr)
                            .trailerUrl(dto.getTrailer_url())
                            .posterUrl(dto.getPoster_url())
                            .genres(movieGenres)
                            .build();

                    // Process actors
                    if (dto.getActors() != null) {
                        for (String actorName : dto.getActors()) {
                            Actor actor = actorRepository.findByFullName(actorName)
                                    .orElseGet(() -> actorRepository.save(
                                            Actor.builder()
                                                    .fullName(actorName)
                                                    .avatarUrl(null)
                                                    .build()
                                    ));
                            MovieActor movieActor = MovieActor.builder()
                                    .movie(movie)
                                    .actor(actor)
                                    .build();
                            movie.getMovieActors().add(movieActor);
                        }
                    }

                    movieRepository.save(movie);
                }
                log.info("Movie data seeding completed successfully.");
            }
        } catch (Exception e) {
            log.error("Failed to seed movie data", e);
        }
    }

    @Data
    public static class MovieJsonDto {
        private String title;
        private String poster_url;
        private String director;
        private List<String> actors;
        private List<String> genre;
        private String release_date;
        private int duration;
        private String language;
        private String synopsis;
        private String trailer_url;
        private List<String> format;
    }
}
