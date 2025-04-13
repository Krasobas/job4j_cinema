package ru.job4j.cinema.dto;

import lombok.Builder;
import lombok.Data;
import ru.job4j.cinema.model.Hall;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class TicketDto {
    private int id;
    private String filmName;
    private int fileId;
    private Hall hall;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int rowNumber;
    private int placeNumber;
    private int userId;
    private int sessionId;
}
