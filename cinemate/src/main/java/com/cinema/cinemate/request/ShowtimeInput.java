package com.cinema.cinemate.request;

import jakarta.validation.constraints.NotNull;
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
public class ShowtimeInput {

    @NotNull(message = "Cinema room is required")
    private UUID roomId;

    @NotNull(message = "Start time is required")
    private OffsetDateTime startTime;
}
