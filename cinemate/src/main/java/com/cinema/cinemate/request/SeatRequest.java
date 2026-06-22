package com.cinema.cinemate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequest {
    private String id;
    private String row;
    private Integer number;
    private String type; // STANDARD, VIP, COUPLE
    private String status; // ACTIVE, MAINTENANCE
}
