package ru.job4j.cinema.dto;

import lombok.Data;

@Data
public class FileDto {
    private String fileName;
    private byte[] fileContent;
}
