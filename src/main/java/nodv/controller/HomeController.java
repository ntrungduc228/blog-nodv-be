package nodv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("")
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> getHomeDefault() {
        return ResponseEntity.ok(new String("Blog NODV Services"));
    }
}