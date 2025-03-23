package ru.job4j.cinema.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FilmSession {
    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "film_id", "filmId",
            "hall_id", "hallId",
            "start_time", "startTime",
            "end_time", "endTime",
            "price", "price"
    );
    private int id;
    private int filmId;
    private int hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int price;
}
