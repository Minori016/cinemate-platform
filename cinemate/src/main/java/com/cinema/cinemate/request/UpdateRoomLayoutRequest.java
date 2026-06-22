package com.cinema.cinemate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomLayoutRequest {
    private Integer rows;
    private Integer cols;
    private List<SeatRequest> seats;
}
