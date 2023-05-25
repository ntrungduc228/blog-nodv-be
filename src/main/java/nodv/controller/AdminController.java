package nodv.controller;

import nodv.model.*;
import nodv.payload.MonthlyCount;
import nodv.payload.SystemResponse;
import nodv.security.TokenProvider;
import nodv.service.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app", "https://blog-nodv-admin.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    ReportingService reportingService;

    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @GetMapping("")
    public String getAdmin() {
        return "Admin Resources !!!";
    }

    @GetMapping("/overview")
    public ResponseEntity<?> overviewSystem(HttpServletRequest request) {
        Long users = userService.countAllUsers();
        Long posts = postService.countAllPosts();
        Long reportings = reportingService.countAllReportings();
        SystemResponse systemResponse = new SystemResponse(users, posts, reportings);
        return new ResponseEntity<>(systemResponse, HttpStatus.OK);
    }

    @GetMapping("/overview/posts")
    public ResponseEntity<?> overviewPosts(HttpServletRequest request) {
        List<MonthlyCount> monthlyCounts = postService.getMonthlyCount();
        return new ResponseEntity<>(monthlyCounts, HttpStatus.OK);
    }

    @GetMapping("/overview/users")
    public ResponseEntity<?> overviewUsers(HttpServletRequest request) {
        List<MonthlyCount> monthlyCounts = userService.getMonthlyCount();
        return new ResponseEntity<>(monthlyCounts, HttpStatus.OK);
    }

    @GetMapping("/overview/reportings")
    public ResponseEntity<?> overviewReportings(HttpServletRequest request) {
        List<MonthlyCount> monthlyCounts = reportingService.getMonthlyCount();
        return new ResponseEntity<>(monthlyCounts, HttpStatus.OK);
    }

    @GetMapping("/reportings")
    public ResponseEntity<?> getReportings(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            HttpServletRequest request) {
//        List<Reporting> reportings = reportingService.getReportingsByPage(page, limit).getContent();
        Page<Reporting> reportings = reportingService.getReportingsByPage(page, limit);
        return new ResponseEntity<>(reportings, HttpStatus.OK);
    }

    @GetMapping("/reporting/{id}")
    public ResponseEntity<?> getReportingById(@PathVariable String id, HttpServletRequest request) throws Exception {
        System.out.println("id " + id);
        Reporting reporting = reportingService.getReportingById(id);
        return new ResponseEntity<>(reporting, HttpStatus.OK);
    }

    @GetMapping("/reporting")
    public ResponseEntity<?> getAllReporting(HttpServletRequest request) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        List<Reporting> reportings = reportingService.getAllReportings();
        return new ResponseEntity<>(reportings, HttpStatus.OK);
    }

    @PatchMapping("/reporting/{id}")
    public ResponseEntity<?> updateReportingStatus(@PathVariable String id, HttpServletRequest request) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Reporting reporting = reportingService.updateReportingState(id);
        return new ResponseEntity<>(reporting, HttpStatus.OK);
    }

    @PostMapping("/warning")
    public ResponseEntity<?> createWarning(@RequestBody Reporting reporting, HttpServletRequest request) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Object object;
        String link = "";
        String receiverId = "";

        switch (reporting.getType()) {
            case POST -> {
                object = (Post) postService.findById(reporting.getObjectId());
                receiverId = ((Post) object).getUserId();
                link = "posts/" + ((Post) object).getId();
                break;
            }
            case COMMENT -> {
                object = commentService.findById(reporting.getObjectId());
                receiverId = ((Comment) object).getUserId();
                link = "posts/" + ((Comment) object).getPostId();

                break;
            }
        }
        // tang so luong warning cho user
        User user = userService.increaseNumOfWarning(receiverId);
        System.out.println(receiverId);
        if (user.getIsActive()) {
            // tao thong bao warning neu chua bi khoa
            Notification notification = new Notification();
            notification.setReceiverId(receiverId);
            notification.setSenderId(userId);
            notification.setType("WARNING");
            notification.setIsRead(false);
            notification.setLink(link);
            Notification newNotification = notificationService.createNotification(notification, userId);
//            User user1 = userService.updateCountNotifications(receiverId, "true");
//            simpMessagingTemplate.convertAndSend("/topic/notifications/" + user.getId() + "/countNotifications", user1);
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + newNotification.getReceiverId() + "/new", newNotification);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/lockedAccount/" + receiverId, receiverId);
        }

        // update status reporting
        reporting = reportingService.updateReportingState(reporting.getId());

        return new ResponseEntity<>(reporting, HttpStatus.OK);
    }

    @PatchMapping("/posts/{id}/lock")
    public ResponseEntity<?> lockPost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.updatePostStatus(id, PostStatus.LOCKED);
        Notification notification = new Notification();
        notification.setReceiverId(post.getUserId());
        notification.setSenderId(userId);
        notification.setType("BLOCK_POST");
        notification.setIsRead(false);
        Notification newNotification = notificationService.createNotification(notification, userId);
        simpMessagingTemplate.convertAndSend("/topic/notifications/" + newNotification.getReceiverId() + "/new", newNotification);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PatchMapping("/posts/{id}/unlock")
    public ResponseEntity<?> unlockPost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.updatePostStatus(id, PostStatus.NORMAL);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/users/allUsers")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
//        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PatchMapping("/users/updateStatusUser/{id}")
    public ResponseEntity<?> updateStatusUser(HttpServletRequest request, @PathVariable String id) {
        User user = userService.updateStatusUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/comments/list")
    public ResponseEntity<?> getComments(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            HttpServletRequest request
    ) {
        Page<Comment> commentPage = commentService.findByFilter(page, limit);
        return new ResponseEntity<>(commentPage, HttpStatus.OK);
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<?> getCommentById(HttpServletRequest request, @PathVariable String id) {
//        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Comment comment = new Comment();
        try {
            comment = commentService.findById(id);
        } catch (Exception ex) {
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id) throws Exception {
        commentService.deleteComment(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
}