package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    boolean existsByTitleVn(String titleVn);
    Optional<Movie> findByTitleVn(String titleVn);

    @Query(value = "SELECT DISTINCT m.* FROM movies m " +
           "LEFT JOIN movie_genres mg ON m.id = mg.movie_id " +
           "WHERE (:search IS NULL OR " +
           "      unaccent(lower(m.title_vn)) ILIKE unaccent(lower(concat('%', cast(:search as varchar), '%'))) OR " +
           "      unaccent(lower(m.title_en)) ILIKE unaccent(lower(concat('%', cast(:search as varchar), '%'))) OR " +
           "      unaccent(lower(m.director)) ILIKE unaccent(lower(concat('%', cast(:search as varchar), '%')))) " +
           "AND (cast(:genreId as uuid) IS NULL OR mg.genre_id = cast(:genreId as uuid)) " +
           "AND (:status IS NULL " +
           "     OR (:status = 'now-showing' AND m.from_date <= :today AND m.to_date >= :today) " +
           "     OR (:status = 'coming-soon' AND m.from_date > :today))",
           countQuery = "SELECT count(DISTINCT m.id) FROM movies m " +
           "LEFT JOIN movie_genres mg ON m.id = mg.movie_id " +
           "WHERE (:search IS NULL OR " +
           "      unaccent(lower(m.title_vn)) ILIKE unaccent(lower(concat('%', cast(:search as varchar), '%'))) OR " +
           "      unaccent(lower(m.title_en)) ILIKE unaccent(lower(concat('%', cast(:search as varchar), '%'))) OR " +
           "      unaccent(lower(m.director)) ILIKE unaccent(lower(concat('%', cast(:search as varchar), '%')))) " +
           "AND (cast(:genreId as uuid) IS NULL OR mg.genre_id = cast(:genreId as uuid)) " +
           "AND (:status IS NULL " +
           "     OR (:status = 'now-showing' AND m.from_date <= :today AND m.to_date >= :today) " +
           "     OR (:status = 'coming-soon' AND m.from_date > :today))",
           nativeQuery = true)
    Page<Movie> findMoviesWithFilters(
            @Param("search") String search,
            @Param("genreId") UUID genreId,
            @Param("status") String status,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}
