package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    java.util.Optional<Genre> findByName(String name);
}
