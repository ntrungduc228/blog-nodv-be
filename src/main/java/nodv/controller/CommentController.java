package nodv.controller;

import nodv.model.Comment;
import nodv.security.TokenProvider;
import nodv.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    TokenProvider tokenProvider;

    //create
    @PostMapping("")
    public ResponseEntity<?> createComment(HttpServletRequest request, @RequestBody Comment comment) throws Exception{
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Comment newComment = commentService.createComment(comment,userId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
    //update
    //update comment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable String id,@RequestBody Comment comment) throws Exception {
        Comment updateComment = commentService.updateComment(id,comment);
        return new ResponseEntity<>(updateComment,HttpStatus.OK);
    }
    //update comment like
    @PutMapping("{id}/like")
    public ResponseEntity<?> updateLikeComment(@PathVariable String id,HttpServletRequest request) throws Exception{
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Comment comment1 = commentService.updatelike(id,userId);
        return new ResponseEntity<>(comment1,HttpStatus.OK);
    }
    //update comment like
    @PutMapping("{id}/unlike")
    public ResponseEntity<?> updateUnlikeComment(@PathVariable String id,HttpServletRequest request) throws Exception{
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Comment comment1 = commentService.updateUnlike(id,userId);
        return new ResponseEntity<>(comment1,HttpStatus.OK);
    }
}
