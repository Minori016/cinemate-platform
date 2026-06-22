package com.cinema.cinemate.controller;

import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.CinemaRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cinema.cinemate.service.CinemaRoomService;

@RestController
@RequestMapping("/api/v1/cinema-rooms")
@RequiredArgsConstructor
public class CinemaRoomController {

    private final CinemaRoomRepository cinemaRoomRepository;
    private final CinemaRoomService cinemaRoomService;

    @GetMapping
    public ApiResponse<List<CinemaRoomResponse>> getCinemaRooms(
            @RequestParam(value = "cinemaId", required = false) UUID cinemaId
    ) {
        var rooms = cinemaId != null
                ? cinemaRoomRepository.findByCinemaId(cinemaId)
                : cinemaRoomRepository.findAll();

        List<CinemaRoomResponse> result = rooms.stream()
                .map(r -> CinemaRoomResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .capacity(r.getCapacity())
                        .cinemaName(r.getCinema() != null ? r.getCinema().getName() : null)
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.<List<CinemaRoomResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CinemaRoomResponse> getCinemaRoomById(@org.springframework.web.bind.annotation.PathVariable UUID id) {
        return ApiResponse.<CinemaRoomResponse>builder()
                .result(cinemaRoomService.getCinemaRoomById(id))
                .build();
    }

    @GetMapping("/{id}/seats")
    public ApiResponse<List<com.cinema.cinemate.response.SeatResponse>> getSeatsByRoomId(@org.springframework.web.bind.annotation.PathVariable UUID id) {
        return ApiResponse.<List<com.cinema.cinemate.response.SeatResponse>>builder()
                .result(cinemaRoomService.getSeatsByRoomId(id))
                .build();
    }
}
