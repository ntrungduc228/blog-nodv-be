package nodv.controller;

import lombok.extern.slf4j.Slf4j;
import nodv.model.User;
import nodv.security.TokenProvider;
import nodv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;
    
    @GetMapping("/{email}")
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.findByEmail(email);
            System.out.println(user);
            return new ResponseEntity<>(userService.findByEmail(email), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("e " + e);
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}