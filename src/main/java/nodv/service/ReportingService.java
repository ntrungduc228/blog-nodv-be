package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.*;
import nodv.payload.MonthlyCount;
import nodv.repository.CommentRepository;
import nodv.repository.ReportingRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportingService {
    @Autowired
    ReportingRepository reportingRepository;

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    CommentRepository commentRepository;
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
        if (!reporting.isPresent()) {
            return null;
        }
        return reporting.get();
    }

    public Page<Reporting> getReportingsByPage(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending());
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

    public void updateStatusComment(String id, String status) {
        Optional<Comment> updateComment = commentRepository.findById(id);
        if (updateComment.isEmpty()) {
            throw new NotFoundException("comment not found");
        }
        updateComment.get().setStatus(status);
        commentRepository.save(updateComment.get());
    }

    public Reporting createReportComment(String userId, String id, String content, ReportType type) {
        updateStatusComment(id, "Reported");
        List<String> listUserIdReported = new ArrayList<String>();
        List<User> listUserReported = new ArrayList<User>();
        List<String> listContentReport = new ArrayList<>();
        User user = userService.findById(userId);
        Reporting reportComment = new Reporting();

//        Optional<Comment> comment = commentRepository.findById(id);
        Reporting checkExist = reportingRepository.findByObjectId(id);
        if (checkExist != null) {
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
        } else {
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

    public Reporting createReport(Reporting reportingData, String userIdActor, ReportType reportType) {
        System.out.println(reportingData.getContent().get(0));
        User user = userService.findById(userIdActor);
        Reporting reporting = reportingRepository.findByObjectId(reportingData.getObjectId());

        if (reporting != null) {
            if (!reporting.getUserIds().contains(userIdActor)) {
                reporting.getUserIds().add(userIdActor);
                reporting.getUsers().add(user);
            }

            if (!reporting.getContent().contains(reportingData.getContent().get(0))) {
                reporting.getContent().add(reportingData.getContent().get(0));
            }
            ;
            reporting.setIsResolved(false);
        } else {
            reporting = new Reporting();
            reporting.setContent(reportingData.getContent());
            reporting.setUserIds(new ArrayList<>());
            reporting.getUserIds().add(userIdActor);
            reporting.setUsers(new ArrayList<>());
            reporting.getUsers().add(user);
            reporting.setIsResolved(false);
            reporting.setType(reportType);
            reporting.setObjectId(reportingData.getObjectId());
        }
        switch (reportType) {
            case POST -> postService.updatePostStatus(reportingData.getObjectId(), PostStatus.REPORTED);
            case COMMENT -> updateStatusComment(reportingData.getObjectId(), "Reported");
        }
        List<User> admins = userService.findAllAdmin();
        admins.forEach(admin -> {
            Notification notification = new Notification();
            notification.setType("REPORTING");
            notification.setReceiverId(admin.getId());
            Notification newNotification = notificationService.createNotification(notification, userIdActor);
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + newNotification.getReceiverId() + "/new", newNotification);
        });
        return reportingRepository.save(reporting);
    }

    public List<MonthlyCount> getMonthlyCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .and(DateOperators.DateToString.dateOf("createdDate").toString("%Y-%m")).as("month"),
                Aggregation.group("month")
                        .count().as("total"),
                Aggregation.project("total").and("_id").as("month")

        );

        AggregationResults<MonthlyCount> results = mongoTemplate.aggregate(
                aggregation, "reporting", MonthlyCount.class
        );

        List<MonthlyCount> monthlyCounts = results.getMappedResults();
        return monthlyCounts;
    }
}