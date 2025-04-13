package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.session.FilmSessionService;
import ru.job4j.cinema.service.ticket.TicketService;

@Log4j2
@Controller
@RequestMapping("/tickets")
@ThreadSafe
@AllArgsConstructor
public class TicketController {
    @GuardedBy("this")
    private final TicketService ticketService;
    @GuardedBy("this")
    private final FilmSessionService filmSessionService;

    @PostMapping("/buy")
    public String buy(@ModelAttribute Ticket ticket, Model model) {
        try {
            log.info(ticket);
            var purchased = ticketService.save(ticket);
            if (purchased.isEmpty()) {
                throw new IllegalArgumentException("Ticket was not purchased");
            }
            return String.format("redirect:/tickets/%d", purchased.get().getUserId());
        } catch (Exception e) {
            log.error(e);
            model.addAttribute("error", "Couldn't book the selected seat. It’s probably taken.");
            return "errors/purchaseFailure";
        }
    }

    @GetMapping("/buy/{sessionId}")
    public String getPurchasePage(@PathVariable int sessionId, Model model) {
        var filmSession = filmSessionService.findById(sessionId);
        if (filmSession.isEmpty()) {
            model.addAttribute("error", "Showtime not found");
            return "errors/404";
        }
        var busyPlaces = ticketService.findBusyPlacesByFilmSessionId(sessionId);
        model.addAttribute("filmSession", filmSession.get())
                .addAttribute("busyPlaces", busyPlaces);
        return "tickets/purchase";
    }

    @GetMapping("/{userId}")
    public String listTickets(@PathVariable int userId, Model model) {
        var tickets = ticketService.findByUserId(userId);
        model.addAttribute("tickets", tickets);
        return "tickets/list";
    }
}
