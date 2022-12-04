package nodv.controller;

import lombok.extern.slf4j.Slf4j;
import nodv.model.Topic;
import nodv.model.User;
import nodv.security.TokenProvider;
import nodv.service.TopicService;
import nodv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    TopicService topicService;

    @Autowired
    TokenProvider tokenProvider;

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
        Page<User> users = userService.search(name, page, limit);
        return new ResponseEntity<>(users.get(), HttpStatus.OK);
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
}