package com.cinema.cinemate.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoGenerateRequest {
    
    @NotNull
    private List<String> roomIds;
    
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
    
    @NotNull
    private LocalTime openTime;
    
    @NotNull
    private LocalTime closeTime;
    
    @NotNull
    private LocalTime goldenHourStart;
    
    @NotNull
    private LocalTime goldenHourEnd;
    
    @NotNull
    private List<String> movieIds;
    
    private String format;
    private String language;
    private java.math.BigDecimal basePrice;
}
