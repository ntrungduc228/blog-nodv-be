package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.*;
import nodv.repository.ReportingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    @Autowired
    private MongoTemplate mongoTemplate;

    public Reporting createReporting(Reporting reporting, String userId, String userIsReportedId) {
        User user = userService.findById(userId);
//        reporting.setUserId(userId);
//        reporting.setUser(user);
//        reporting.setUserIsReportedId(userIsReportedId);
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

    private Reporting findExistingReport(Reporting newReport) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(newReport.getType())
                .and("content").is(newReport.getContent()));
        return mongoTemplate.findOne(query, Reporting.class);
    }

    public Reporting createReport(Reporting newReport, String userId) {
        User user = userService.findById(userId);
        Reporting existingReport = findExistingReport(newReport);
        Reporting reporting = new Reporting();
        if (existingReport != null) {
            existingReport.getUserIds().add(user.getId());
            existingReport.getUsers().add(user);
            reporting = mongoTemplate.save(existingReport);

        } else {
            newReport.getUserIds().add(user.getId());
            newReport.getUsers().add(user);
            reporting = mongoTemplate.save(newReport);
        }

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
        return reporting;
    }
}