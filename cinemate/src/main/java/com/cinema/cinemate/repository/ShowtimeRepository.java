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

    List<Showtime> findByMovieId(UUID movieId);
}
