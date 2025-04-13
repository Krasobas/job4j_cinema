package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.cinema.service.file.FileService;

@RestController
@RequestMapping("/files")
@ThreadSafe
@AllArgsConstructor
public class FileController {
    @GuardedBy("this")
    private final FileService fileService;

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getById(@PathVariable int fileId) {
        var contentOptional = fileService.getFileById(fileId);
        if (contentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contentOptional.get().getContent());
    }
}