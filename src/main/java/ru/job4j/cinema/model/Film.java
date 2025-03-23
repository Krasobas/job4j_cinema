package ru.job4j.cinema.model;

import lombok.Data;

import java.util.Map;

@Data
public class Film {
    public static final Map<String, String> COLUMN_MAPPING = Map.of(
      "id", "id",
      "name", "name",
      "description", "description",
      "year", "year",
      "genre_id", "genreId",
      "minimal_age", "minimalAge",
      "duration_in_minutes", "durationInMinutes",
      "file_id", "fileId"
    );
    private int id;
    private String name;
    private String description;
    private int year;
    private int genreId;
    private int minimalAge;
    private int durationInMinutes;
    private int fileId;
}
