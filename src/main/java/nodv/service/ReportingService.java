package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.*;
import nodv.repository.ReportingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Long countAllReportings() {
        return reportingRepository.count();
    }

    public Reporting getReportingById(String id) {
        Optional<Reporting> reporting = reportingRepository.findById(id);
        if(!reporting.isPresent()) {
            return null;
        }
        return reporting.get();
    }

    public Page<Reporting> getReportingsByPage(int page, int limit){
        Pageable pageable = PageRequest.of(page, limit);
        return reportingRepository.findAll(pageable);
    }

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

    public Reporting createReportComment(String userId, String id, String content, ReportType type){
        List<String> listUserIdReported = new ArrayList<String>();
        List<User> listUserReported = new ArrayList<User>();
        List<String> listContentReport = new ArrayList<>();
        User user = userService.findById(userId);
        Reporting reportComment = new Reporting();

//        Optional<Comment> comment = commentRepository.findById(id);
        Reporting checkExist = reportingRepository.findByObjectId(id);
        if (checkExist != null){
            listUserIdReported = checkExist.getUserIds();
            listUserIdReported.add(userId);
            listUserReported = checkExist.getUsers();
            listUserReported.add(user);
            listContentReport = checkExist.getContent();
            listContentReport.add(content);
            checkExist.setUserIds(listUserIdReported);
            checkExist.setContent(listContentReport);
            checkExist.setUsers(listUserReported);

            reportComment = checkExist;
        }else {
            listUserIdReported.add(userId);
            listUserReported.add(user);
            listContentReport.add(content);
            reportComment.setUserIds(listUserIdReported);
            reportComment.setContent(listContentReport);
            reportComment.setIsResolved(false);
            reportComment.setType(type);
            reportComment.setObjectId(id);
            reportComment.setUsers(listUserReported);
        }

        return reportingRepository.save(reportComment);
    }
}