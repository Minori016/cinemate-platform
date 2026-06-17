package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActorRepository extends JpaRepository<Actor, UUID> {
    Optional<Actor> findByFullName(String fullName);
}
