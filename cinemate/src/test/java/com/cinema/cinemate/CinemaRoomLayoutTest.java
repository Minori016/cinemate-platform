package com.cinema.cinemate;

import com.cinema.cinemate.entity.CinemaRoom;
import com.cinema.cinemate.repository.CinemaRoomRepository;
import com.cinema.cinemate.request.SeatRequest;
import com.cinema.cinemate.request.UpdateRoomLayoutRequest;
import com.cinema.cinemate.service.CinemaRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
public class CinemaRoomLayoutTest {

    @Autowired
    private CinemaRoomService cinemaRoomService;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    @Test
    public void testLayoutUpdate() {
        CinemaRoom room = new CinemaRoom();
        room.setName("Test Room");
        room.setCapacity(0);
        room = cinemaRoomRepository.save(room);

        UpdateRoomLayoutRequest request = new UpdateRoomLayoutRequest();
        request.setRows(1);
        request.setCols(1);
        SeatRequest seat = new SeatRequest();
        seat.setRow("A");
        seat.setNumber(1);
        seat.setType("STANDARD");
        seat.setStatus("ACTIVE");
        request.setSeats(Arrays.asList(seat));

        System.out.println("--- FIRST UPDATE ---");
        cinemaRoomService.updateRoomLayout(room.getId(), request);

        System.out.println("--- DELETE ---");
        try {
            cinemaRoomService.deleteCinemaRoom(room.getId());
            System.out.println("Delete success");
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }
}
