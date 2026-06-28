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
public class ManualShowtimeCreateRequest {
    
    @NotNull(message = "Movie ID is required")
    private UUID movieId;
    
    @NotNull(message = "Room ID is required")
    private UUID roomId;
    
    @NotNull(message = "Start time is required")
    @jakarta.validation.constraints.Future(message = "Thời gian bắt đầu phải ở tương lai, không thể tạo suất chiếu trong quá khứ")
    private OffsetDateTime startTime;
    
    @jakarta.validation.constraints.NotBlank(message = "Format is required")
    private String format;
    
    @jakarta.validation.constraints.NotBlank(message = "Language is required")
    private String language;
    
    @NotNull(message = "Base price is required")
    @jakarta.validation.constraints.Min(value = 0, message = "Base price must be positive")
    private java.math.BigDecimal basePrice;
}
