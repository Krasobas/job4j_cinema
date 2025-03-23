package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.service.film.FilmService;

import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/films")
@ThreadSafe
@AllArgsConstructor
public class FilmController {
    @GuardedBy("this")
    private FilmService filmService;

    @GetMapping
    public String getAll(Model model) {
        Collection<Film> films = filmService.findAll();
        model.addAttribute("films", films);
        return "films/list";
    }

    @GetMapping("/{filmId}")
    public String getFilm(@PathVariable int filmId, Model model) {
        var film = filmService.findById(filmId);
        if (film.isEmpty()) {
            model.addAttribute("error", "Film not found");
            return "errors/404";
        }
        model.addAttribute("film", film.get());
        return "films/one";
    }
}
