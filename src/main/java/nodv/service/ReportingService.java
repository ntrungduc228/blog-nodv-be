package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.*;
import nodv.repository.ReportingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportingService {
    @Autowired
    ReportingRepository reportingRepository;

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;

    @Autowired
    NotificationService notificationService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public Reporting createReporting(Reporting reporting, String userId, String userIsReportedId) {
        User user = userService.findById(userId);
        reporting.setUserId(userId);
        reporting.setUser(user);
        reporting.setUserIsReportedId(userIsReportedId);
        reporting.setIsResolved(false);
        if (reporting.getType().equals(ReportType.POST)) {
            postService.updatePostStatus(reporting.getObjectId(), PostStatus.REPORTED);
        }
        List<User> admins = userService.findAllAdmin();
        admins.forEach(admin -> {
            Notification notification = new Notification();
            notification.setType("REPORTING");
            notification.setReceiverId(admin.getId());
            Notification newNotification = notificationService.createNotification(notification, userId);
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + newNotification.getReceiverId() + "/new", newNotification);
        });
        return reportingRepository.save(reporting);
    }

    public List<Reporting> getAllReportings() {
        List<Reporting> reportings = reportingRepository.findAll();
        return reportings;
    }

    public Reporting updateReportingState(String reportingId) {
        Optional<Reporting> reporting = reportingRepository.findById(reportingId);
        if (reporting.isPresent()) {
            reporting.get().setIsResolved(!reporting.get().getIsResolved());
        } else {
            throw new NotFoundException("Reporting not found");
        }
        return reportingRepository.save(reporting.get());
    }
}