package com.cinema.cinemate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_actors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieActor {

    @EmbeddedId
    @Builder.Default
    private MovieActorId id = new MovieActorId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actorId")
    @JoinColumn(name = "actor_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Actor actor;

    @Column(name = "character_name", length = 150)
    private String characterName;
}
