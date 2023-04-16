package nodv.controller;

import nodv.model.Comment;
import nodv.model.Notification;
import nodv.model.Reporting;
import nodv.model.User;
import nodv.payload.SystemResponse;
import nodv.security.TokenProvider;
import nodv.service.NotificationService;
import nodv.service.PostService;
import nodv.service.ReportingService;
import nodv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    ReportingService reportingService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("")
    public String getAdmin(){
        return "Admin Resources !!!";
    }

    @GetMapping("/overview")
    public ResponseEntity<?> overviewSystem(HttpServletRequest request) {
        Long users = userService.countAllUsers();
        Long posts = postService.countAllPosts();
        Long reportings = reportingService.countAllReportings();x
        SystemResponse systemResponse = new SystemResponse(users, posts, reportings);
        return new ResponseEntity<>(systemResponse, HttpStatus.OK);
    }

    @GetMapping("/reporting")
    public ResponseEntity<?> getAllReporting(HttpServletRequest request) {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        List<Reporting> reportings = reportingService.getAllReportings();
        return new ResponseEntity<>(reportings, HttpStatus.OK);
    }

    @GetMapping("/reporting/{id}")
    public ResponseEntity<?> getReportingById(@PathVariable String id, HttpServletRequest request) throws Exception {
        System.out.println("id " + id);
        Reporting reporting =  reportingService.getReportingById(id);
        return new ResponseEntity<>(reporting, HttpStatus.OK);
    }

    @GetMapping("/reportings")
    public ResponseEntity<?> getReportings(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            HttpServletRequest request) {
        List<Reporting> reportings = reportingService.getReportingsByPage(page, limit).getContent();
        return new ResponseEntity<>(reportings, HttpStatus.OK);
    }

    @PatchMapping("/reporting/{id}")
    public ResponseEntity<?> updateReportingStatus(@PathVariable String id, HttpServletRequest request) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Reporting reporting =  reportingService.updateReportingState(id);
        return new ResponseEntity<>(reporting, HttpStatus.OK);
    }

    @PostMapping("/warning")
    public ResponseEntity<?> createWarning( @RequestBody Notification notification){
        // tang so luong warning cho user
        String receiverId = notification.getReceiverId();
        User user = userService.increaseNumOfWarning(receiverId);
//        if(!user.getIsActive()) {
            // tao thong bao warning neu chua bi khoa
            String userId = "637c797126f4ca37f32d8d16";
            Notification newNotification = notificationService.createNotification(notification, userId);
            User user1 = userService.updateCountNotifications(receiverId, "true");
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + user.getId() + "/countNotifications", user1);
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + newNotification.getReceiverId() + "/new", newNotification);
//        }else {
//            simpMessagingTemplate.convertAndSend("/topic/lockedAccount/" + receiverId, receiverId);
//        }


        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}