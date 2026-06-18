package com.cinema.cinemate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeResponse {
    private UUID id;
    private UUID roomId;
    private String roomName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String status;
}
