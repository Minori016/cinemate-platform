package com.cinema.cinemate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "score_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "type", nullable = false, length = 10)
    private String type; // "EARN" or "SPEND"

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "movie_name", nullable = false)
    private String movieName;

    @CreationTimestamp
    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime date;
}
