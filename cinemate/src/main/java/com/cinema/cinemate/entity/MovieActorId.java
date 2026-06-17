package com.cinema.cinemate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieActorId implements Serializable {
    @Column(name = "movie_id")
    private UUID movieId;

    @Column(name = "actor_id")
    private UUID actorId;
}
