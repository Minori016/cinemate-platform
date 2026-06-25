package com.cinema.cinemate.repository;

import com.cinema.cinemate.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {

    /**
     * Kiểm tra xem phòng chiếu có lịch trùng trong khoảng thời gian không.
     * Trùng khi: existing.startTime < newEndTime AND existing.endTime > newStartTime
     */
    boolean existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID roomId, OffsetDateTime endTime, OffsetDateTime startTime);

    /**
     * Thuật toán Overlap CÓ TÍNH DỌN RẠP (15 phút)
     * Khoảng thời gian yêu cầu: [reqStartMinusCleaning, reqEndWithCleaning]
     * Một suất chiếu cũ [oldStart, oldEnd] bị coi là trùng lấp nếu:
     * reqStartMinusCleaning < oldEnd AND reqEndWithCleaning > oldStart
     */
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId " +
           "AND s.startTime < :reqEndWithCleaning " +
           "AND s.endTime > :reqStartMinusCleaning")
    boolean hasOverlapWithCleaning(
            @org.springframework.data.repository.query.Param("roomId") UUID roomId, 
            @org.springframework.data.repository.query.Param("reqStartMinusCleaning") OffsetDateTime reqStartMinusCleaning,
            @org.springframework.data.repository.query.Param("reqEndWithCleaning") OffsetDateTime reqEndWithCleaning);

    List<Showtime> findByMovieId(UUID movieId);
}
