package ru.job4j.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.cinema.model.Genre;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {
    private int id;
    private String name;
    private String description;
    private int year;
    private Genre genre;
    private int minimalAge;
    private String duration;
    private int fileId;
}
