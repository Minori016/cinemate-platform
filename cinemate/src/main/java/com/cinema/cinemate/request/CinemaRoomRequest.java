package com.cinema.cinemate.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CinemaRoomRequest {
    private UUID cinemaId; // Optional for now, since maybe there's only one cinema

    @NotBlank(message = "Room name is required")
    private String name;
    
    private Integer capacity;
}
