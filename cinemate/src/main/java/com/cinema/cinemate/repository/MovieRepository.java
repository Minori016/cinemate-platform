package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    boolean existsByTitleVn(String titleVn);
    java.util.Optional<Movie> findByTitleVn(String titleVn);
}
