package nodv.controller;

import nodv.controller.model.Notification;
import nodv.security.TokenProvider;
import nodv.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //get notifications
    @GetMapping("")
    public ResponseEntity<?> getNotifications(
            HttpServletRequest request,
            @RequestParam(value = "isRead", required = false) String isRead,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        List<Notification> notifications = notificationService.findByReceiverId(userId, isRead, page, limit);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }


    //create notification
    @PostMapping("")
    public ResponseEntity<?> createNotification(HttpServletRequest request, @RequestBody Notification notification) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Notification newNotification = notificationService.createNotification(notification, userId);
        System.out.println("id receiver " + newNotification.getReceiverId());
        simpMessagingTemplate.convertAndSend("/topic/notifications/" + newNotification.getReceiverId() + "/new", newNotification);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    //update
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable String id) throws Exception {
        Notification updateNotification = notificationService.updateNotification(id);
        return new ResponseEntity<>(updateNotification, HttpStatus.OK);
    }

}
