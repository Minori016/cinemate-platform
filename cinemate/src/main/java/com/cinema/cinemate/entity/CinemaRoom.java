package com.cinema.cinemate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cinema_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinemaRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cinema cinema;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
