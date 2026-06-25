package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.CinemaRoom;
import com.cinema.cinemate.entity.Seat;
import com.cinema.cinemate.enums.SeatType;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.repository.SeatRepository;
import com.cinema.cinemate.request.SeatRequest;
import com.cinema.cinemate.request.UpdateRoomLayoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cinema.cinemate.response.CinemaRoomResponse;
import com.cinema.cinemate.response.SeatResponse;
import com.cinema.cinemate.request.CinemaRoomRequest;
import com.cinema.cinemate.entity.Cinema;
import com.cinema.cinemate.repository.CinemaRepository;

@Service
@RequiredArgsConstructor
public class CinemaRoomService {

    private final CinemaRoomRepository cinemaRoomRepository;
    private final SeatRepository seatRepository;
    private final CinemaRepository cinemaRepository;

    @Transactional
    public void updateRoomLayout(UUID roomId, UpdateRoomLayoutRequest request) {
        CinemaRoom room = cinemaRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Cinema room not found"));

        // Update room capacity & grid size
        room.setRowCount(request.getRows());
        room.setColumnCount(request.getCols());
        room.setCapacity(request.getSeats().size());
        cinemaRoomRepository.save(room);

        // Wipe existing seats
        seatRepository.deleteByRoomId(roomId);
        seatRepository.flush();

        // Insert new seats
        List<Seat> newSeats = new ArrayList<>();
        java.util.Set<String> seenSeats = new java.util.HashSet<>();
        for (SeatRequest sr : request.getSeats()) {
            String seatKey = sr.getRow() + "-" + sr.getNumber();
            if (!seenSeats.add(seatKey)) {
                continue; // Skip duplicate seats
            }
            SeatType type;
            try {
                type = SeatType.valueOf(sr.getType().toUpperCase());
            } catch (Exception e) {
                type = SeatType.STANDARD; // Fallback
            }

            boolean isActive = true;
            if ("MAINTENANCE".equalsIgnoreCase(sr.getStatus())) {
                isActive = false;
            }

            Seat seat = Seat.builder()
                    .room(room)
                    .rowLabel(sr.getRow())
                    .seatNumber(sr.getNumber())
                    .seatType(type)
                    .isActive(isActive)
                    .build();

            newSeats.add(seat);
        }

        if (!newSeats.isEmpty()) {
            seatRepository.saveAll(newSeats);
        }
    }

    public CinemaRoomResponse getCinemaRoomById(UUID id) {
        CinemaRoom room = cinemaRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema room not found"));
        return mapToResponse(room);
    }

    public List<SeatResponse> getSeatsByRoomId(UUID roomId) {
        List<Seat> seats = seatRepository.findByRoomId(roomId);
        return seats.stream().map(seat -> SeatResponse.builder()
                .id(seat.getRowLabel() + seat.getSeatNumber())
                .row(seat.getRowLabel())
                .number(seat.getSeatNumber())
                .type(seat.getSeatType().name())
                .status(seat.getIsActive() ? "ACTIVE" : "MAINTENANCE")
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public CinemaRoomResponse addCinemaRoom(CinemaRoomRequest request) {
        Cinema cinema = null;
        if (request.getCinemaId() != null) {
            cinema = cinemaRepository.findById(request.getCinemaId())
                    .orElseThrow(() -> new RuntimeException("Cinema not found"));
        } else {
            List<Cinema> cinemas = cinemaRepository.findAll();
            if (!cinemas.isEmpty()) {
                cinema = cinemas.get(0);
            }
        }

        CinemaRoom room = CinemaRoom.builder()
                .cinema(cinema)
                .name(request.getName())
                .capacity(request.getCapacity() != null ? request.getCapacity() : 0)
                .rowCount(0)
                .columnCount(0)
                .build();

        cinemaRoomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public CinemaRoomResponse updateCinemaRoom(UUID id, CinemaRoomRequest request) {
        CinemaRoom room = cinemaRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema room not found"));

        if (request.getCinemaId() != null) {
            Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                    .orElseThrow(() -> new RuntimeException("Cinema not found"));
            room.setCinema(cinema);
        }

        room.setName(request.getName());
        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }

        cinemaRoomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public void deleteCinemaRoom(UUID id) {
        if (!cinemaRoomRepository.existsById(id)) {
            throw new RuntimeException("Cinema room not found");
        }
        seatRepository.deleteByRoomId(id);
        cinemaRoomRepository.deleteById(id);
    }

    private CinemaRoomResponse mapToResponse(CinemaRoom room) {
        return CinemaRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .cinemaName(room.getCinema() != null ? room.getCinema().getName() : null)
                .build();
    }
}
