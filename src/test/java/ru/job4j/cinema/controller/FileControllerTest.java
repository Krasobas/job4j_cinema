package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.file.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class FileControllerTest {

    private FileController controller;

    private FileService fileService;

    @BeforeEach
    void init() {
        fileService = mock(FileService.class);
        controller = new FileController(fileService);
    }

    @Test
    void whenGetFileByIdThenGotItInResponseBody() {
        var expectedFile = new FileDto("name", "content".getBytes());

        doReturn(Optional.of(expectedFile)).when(fileService).getFileById(anyInt());
        var expectedResponse = ResponseEntity.ok(expectedFile.getContent());

        var gotResponse = controller.getById(1);

        assertThat(gotResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void whenNotGetFileByIdThenGotNotFound() {
        doReturn(Optional.empty()).when(fileService).getFileById(anyInt());
        var expectedResponse = ResponseEntity.notFound().build();

        var gotResponse = controller.getById(1);

        assertThat(gotResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
    }
}