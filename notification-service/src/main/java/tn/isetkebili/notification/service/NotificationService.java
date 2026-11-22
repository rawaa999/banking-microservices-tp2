package tn.isetkebili.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.isetkebili.notification.entity.Notification;
import tn.isetkebili.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification send(Notification notification) {
        notification.setSentAt(LocalDateTime.now());
        Notification saved = repository.save(notification);
        log.info("Notification envoyée au client {} : {}", saved.getCustomerId(), saved.getMessage());
        return saved;
    }

    public List<Notification> findAll() {
        return repository.findAll();
    }

    public List<Notification> findByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId);
    }
}