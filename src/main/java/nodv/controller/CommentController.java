package nodv.controller;

import nodv.exception.ErrorResponse;
import nodv.model.Comment;
import nodv.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;
    @GetMapping("")
    public ResponseEntity<?> findAll(){
        List<Comment> list = commentService.findAll();


        if (list.isEmpty()) {
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
           // throw new ErrorResponse(HttpStatus.NO_CONTENT, "No comment founded", 404);
        }


        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody Comment comment){
        return new ResponseEntity<>(commentService.create(comment), HttpStatus.CREATED);
    }

}