package nodv.controller;

import lombok.extern.slf4j.Slf4j;
import nodv.model.Post;
import nodv.model.User;
import nodv.security.TokenProvider;
import nodv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

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

    @PatchMapping("/follow/{idFollow}")
    public ResponseEntity<?> followUser(@PathVariable String idFollow, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User user = userService.followUser(userId, idFollow);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/unfollow/{idUnfollow}")
    public ResponseEntity<?> unFollowUser(@PathVariable String idUnfollow, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User user = userService.unfollowUser(userId, idUnfollow);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}