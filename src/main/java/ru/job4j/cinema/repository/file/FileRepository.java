package ru.job4j.cinema.repository.file;



import ru.job4j.cinema.model.File;

import java.util.Optional;

public interface FileRepository {
    File save(File file);

    Optional<File> findById(int id);

    void deleteById(int id);
}
