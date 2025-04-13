package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.service.film.FilmService;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.session.FilmSessionService;

import java.util.Collection;

@Controller
@RequestMapping("/sessions")
@ThreadSafe
@AllArgsConstructor
public class FilmSessionController {
    @GuardedBy("this")
    private FilmSessionService filmSessionService;

    @GetMapping
    public String getAll(Model model) {
        Collection<FilmSessionDto> filmSessions = filmSessionService.findAll();
        model.addAttribute("filmSessions", filmSessions);
        return "sessions/list";
    }
}
