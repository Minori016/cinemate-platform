package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.ScoreHistory;
import com.cinema.cinemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, UUID> {

    @Query("SELECT s FROM ScoreHistory s WHERE s.user = :user " +
           "AND s.type = :type " +
           "AND s.date >= :startDate AND s.date <= :endDate " +
           "ORDER BY s.date DESC")
    List<ScoreHistory> findFilteredHistory(
            @Param("user") User user,
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    boolean existsByUser(User user);
}
