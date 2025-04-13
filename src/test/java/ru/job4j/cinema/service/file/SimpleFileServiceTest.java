package ru.job4j.cinema.service.file;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimpleFileServiceTest {
    private static SimpleFileService service;
    private static Sql2oFileRepository repository;
    private static String filesDirectory;

    @BeforeAll
    static void setUp() throws IOException {
        filesDirectory = Files.createTempDirectory("cinema-tests-").toAbsolutePath().toString();
        repository = mock(Sql2oFileRepository.class);
        service = new SimpleFileService(repository, filesDirectory);

    }

    @Test
    void whenSaveThenReturnFile() {
        var expected = new File("name", "path");
        var fileCaptor = ArgumentCaptor.forClass(File.class);
        doReturn(expected).when(repository).save(fileCaptor.capture());

        var got = service.save(new FileDto("name", "content".getBytes()));

        assertThat(fileCaptor.getValue().getName()).isEqualTo(expected.getName());
        assertThat(fileCaptor.getValue().getPath()).startsWith(filesDirectory);
        assertThat(expected).usingRecursiveComparison().isEqualTo(got);
    }

    @Test
    void whenGetFileByIdThenGotFileDto() throws IOException {
        var filePath = Path.of(filesDirectory, "test.txt");
        var fileContent = "content".getBytes();
        Files.write(filePath, fileContent);
        var expected = new FileDto("name", fileContent);
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        doReturn(Optional.of(new File("name", filePath.toString()))).when(repository).findById(idCaptor.capture());

        var got = service.getFileById(11);

        assertThat(idCaptor.getValue()).isEqualTo(11);
        assertThat(got).isPresent();
        assertThat(got.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenDeleteByIdThenOk() throws IOException {
        var filePath = Path.of(filesDirectory, "test.txt");
        var fileContent = new byte[]{};
        Files.write(filePath, fileContent);

        var findCaptor = ArgumentCaptor.forClass(Integer.class);
        doReturn(Optional.of(new File("name", filePath.toString()))).when(repository).findById(findCaptor.capture());
        var deleteCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(repository).deleteById(deleteCaptor.capture());

        service.deleteById(11);

        assertThat(findCaptor.getValue()).isEqualTo(11);
        assertThat(deleteCaptor.getValue()).isEqualTo(11);
        assertThat(Files.exists(filePath)).isFalse();
    }
}