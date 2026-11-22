package tn.isetkebili.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.isetkebili.notification.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomerId(Long customerId);
}