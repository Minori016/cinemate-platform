package com.cinema.cinemate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistoryResponse {
    private String id;
    private String type; // "EARN" or "SPEND"
    private Integer amount;
    private String movieName;
    private LocalDateTime date;
}
