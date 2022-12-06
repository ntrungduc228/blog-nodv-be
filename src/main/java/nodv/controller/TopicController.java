package nodv.controller;

import nodv.model.Topic;
import nodv.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/topics")
public class TopicController {
    @Autowired
    TopicService topicService;

    @GetMapping("")
    ResponseEntity<?> getTopics() {
        List<Topic> topics = topicService.findAll();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @GetMapping("/search")
    ResponseEntity<?> searchTopics(@RequestParam(value = "q", required = true) String name) {
        List<Topic> topics = topicService.searchByName(name);
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }


}
