package tn.isetkebili.notification.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.isetkebili.notification.entity.Notification;
import tn.isetkebili.notification.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification send(@Valid @RequestBody Notification notification) {
        return service.send(notification);
    }

    @GetMapping
    public List<Notification> findAll() {
        return service.findAll();
    }

    @GetMapping("/customer/{customerId}")
    public List<Notification> findByCustomer(@PathVariable Long customerId) {
        return service.findByCustomerId(customerId);
    }
}