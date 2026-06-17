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

    @Query("SELECT DISTINCT m FROM Movie m " +
           "LEFT JOIN m.genres g " +
           "WHERE (:search IS NULL OR LOWER(m.titleVn) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(m.titleEn) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(m.director) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (:genreId IS NULL OR g.id = :genreId) " +
           "AND (:status IS NULL " +
           "     OR (:status = 'now-showing' AND m.fromDate <= :today AND m.toDate >= :today) " +
           "     OR (:status = 'coming-soon' AND m.fromDate > :today))")
    Page<Movie> findMoviesWithFilters(
            @Param("search") String search,
            @Param("genreId") UUID genreId,
            @Param("status") String status,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}
