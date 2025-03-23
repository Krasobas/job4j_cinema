package ru.job4j.cinema.model;

import lombok.Data;

import java.util.Map;

@Data
public class Hall {
    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "name", "name",
            "row_count", "rowCount",
            "place_count", "placeCount",
            "description", "description"
    );
    private int id;
    private String name;
    private int rowCount;
    private int placeCount;
    private String description;
}
