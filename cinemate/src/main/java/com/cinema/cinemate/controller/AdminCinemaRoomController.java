package com.cinema.cinemate.controller;

import com.cinema.cinemate.request.UpdateRoomLayoutRequest;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.service.CinemaRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cinema-rooms")
@RequiredArgsConstructor
public class AdminCinemaRoomController {

    private final CinemaRoomService cinemaRoomService;

    @PutMapping("/{roomId}/layout")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateRoomLayout(
            @PathVariable UUID roomId,
            @RequestBody UpdateRoomLayoutRequest request) {
        
        cinemaRoomService.updateRoomLayout(roomId, request);
        
        return ApiResponse.<String>builder()
                .result("Room layout updated successfully")
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.cinema.cinemate.response.CinemaRoomResponse> createRoom(
            @jakarta.validation.Valid @RequestBody com.cinema.cinemate.request.CinemaRoomRequest request) {
        return ApiResponse.<com.cinema.cinemate.response.CinemaRoomResponse>builder()
                .message("Room created successfully")
                .result(cinemaRoomService.addCinemaRoom(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.cinema.cinemate.response.CinemaRoomResponse> updateRoom(
            @PathVariable UUID id,
            @jakarta.validation.Valid @RequestBody com.cinema.cinemate.request.CinemaRoomRequest request) {
        return ApiResponse.<com.cinema.cinemate.response.CinemaRoomResponse>builder()
                .message("Room updated successfully")
                .result(cinemaRoomService.updateCinemaRoom(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteRoom(@PathVariable UUID id) {
        cinemaRoomService.deleteCinemaRoom(id);
        return ApiResponse.<Void>builder()
                .message("Room deleted successfully")
                .build();
    }
}
