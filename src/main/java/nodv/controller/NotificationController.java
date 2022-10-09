package nodv.controller;

import nodv.exception.NotFoundException;
import nodv.model.Notification;
import nodv.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    NotificationRepository notificationRepository;

    @GetMapping("")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();
        notificationRepository.findAll().forEach(notifications::add);
        if (notifications.isEmpty()) {
            throw new NotFoundException("Test");
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Notification>> getNotificationsOfUser(String Id) {
        try {
            List<Notification> notifications = new ArrayList<>();
            notificationRepository.findByReceiverId(Id).forEach(notifications::add);
            if (notifications.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        try {
            Notification newNotification = notificationRepository.save(notification);
            return new ResponseEntity<>(newNotification, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
