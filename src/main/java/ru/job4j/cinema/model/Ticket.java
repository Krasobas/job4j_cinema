package ru.job4j.cinema.model;

import lombok.Data;

import java.util.Map;

@Data
public class Ticket {
    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "session_id", "sessionId",
            "row_number", "rowNumber",
            "place_number", "placeNumber",
            "user_id", "userId"
    );
    private int id;
    private int sessionId;
    private int rowNumber;
    private int placeNumber;
    private int userId;
}
