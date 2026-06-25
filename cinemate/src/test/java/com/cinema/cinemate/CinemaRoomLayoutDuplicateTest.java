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

@SpringBootTest
public class CinemaRoomLayoutDuplicateTest {

    @Autowired
    private CinemaRoomService cinemaRoomService;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    @Test
    public void testLayoutUpdateDuplicate() {
        CinemaRoom room = new CinemaRoom();
        room.setName("Test Room Dup");
        room.setCapacity(0);
        room = cinemaRoomRepository.save(room);

        UpdateRoomLayoutRequest request = new UpdateRoomLayoutRequest();
        request.setRows(1);
        request.setCols(2);
        SeatRequest seat1 = new SeatRequest();
        seat1.setRow("A");
        seat1.setNumber(1);
        seat1.setType("STANDARD");
        seat1.setStatus("ACTIVE");
        
        SeatRequest seat2 = new SeatRequest();
        seat2.setRow("A"); // Duplicate row
        seat2.setNumber(1); // Duplicate number
        seat2.setType("STANDARD");
        seat2.setStatus("ACTIVE");
        
        request.setSeats(Arrays.asList(seat1, seat2));

        System.out.println("--- DUP UPDATE ---");
        try {
            cinemaRoomService.updateRoomLayout(room.getId(), request);
            System.out.println("Dup update success");
        } catch (Exception e) {
            System.out.println("Dup update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
