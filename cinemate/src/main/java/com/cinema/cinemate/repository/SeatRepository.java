package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByRoomId(UUID roomId);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Seat s WHERE s.room.id = :roomId")
    void deleteByRoomId(@org.springframework.data.repository.query.Param("roomId") UUID roomId);
}
