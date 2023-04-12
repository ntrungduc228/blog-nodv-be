package nodv.controller;

import lombok.extern.slf4j.Slf4j;
import nodv.model.Topic;
import nodv.model.User;
import nodv.projection.UserProjection;
import nodv.repository.UserRepository;
import nodv.security.TokenProvider;
import nodv.service.TopicService;
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
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestBody User user, HttpServletRequest request) {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        User updateUser = userService.updateBasicProfile(user, userId);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(
            @RequestParam(value = "q", required = true) String name,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit
    ) {
        Page<UserProjection> users = userService.search(name, page, limit);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/getUsersNotFollowed")
    public ResponseEntity<?> getUsersNotFollowed(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "5", required = false) int limit) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        System.out.println(userId);

        List<User> FollowingId = userService.getUsesNotFollowed(userId, limit);

        return new ResponseEntity<>(FollowingId, HttpStatus.OK);
    }

    @PatchMapping("/follow/{followId}")
    public ResponseEntity<?> followUser(@PathVariable String followId, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User user = userService.followUser(userId, followId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/unFollow/{unFollowId}")
    public ResponseEntity<?> unFollowUser(@PathVariable String unFollowId, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User user = userService.unFollowUser(userId, unFollowId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/topics")
    public ResponseEntity<?> getOwnTopics(HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        List<Topic> topics = topicService.findUserTopics(userId);
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @PatchMapping("/topics")
    public ResponseEntity<?> setTopics(@RequestBody User user, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User userUpdate = userService.setTopics(user, userId);
        return new ResponseEntity<>(userUpdate, HttpStatus.OK);
    }


    // update count numOfNotifications
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateCountNotifications(@PathVariable String userId, @RequestParam(value = "isIncrease", required = false) String isIncrease) {
        User user = userService.updateCountNotifications(userId, isIncrease);
        simpMessagingTemplate.convertAndSend("/topic/notifications/" + user.getId() + "/countNotifications", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //    get user follower
    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowers(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit,
            @PathVariable String id) {
        Page<UserProjection> followers = userService.getFollowers(page, limit, id);
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<?> getAllUserFollowing(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit,
            @PathVariable String id) {
        Page<UserProjection> follower = userService.getFollowing(page, limit, id);
        return new ResponseEntity<>(follower, HttpStatus.OK);
    }


    @PatchMapping("/topics/{topicId}")
    public ResponseEntity<?> addTopics(HttpServletRequest request, @PathVariable String topicId) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User userUpdate = userService.followTopics(topicId, userId);
        return new ResponseEntity<>(userUpdate, HttpStatus.OK);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
//        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PatchMapping("/updateStatusUser/{id}")
    public ResponseEntity<?> updateStatusUser(HttpServletRequest request, @PathVariable String id) {
        User user = userService.updateStatusUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);


    }
}