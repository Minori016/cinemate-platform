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
public class ShowtimePreviewResponse {
    private UUID tempId;
    private UUID movieId;
    private String movieTitle;
    private UUID roomId;
    private String roomName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private double priorityScore;
    private String format;
    private String language;
    private boolean isGoldenHour;
    private java.math.BigDecimal basePrice;
}
