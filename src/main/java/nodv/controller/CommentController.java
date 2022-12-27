package nodv.controller;

import nodv.model.Comment;
import nodv.security.TokenProvider;
import nodv.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("api/comments")
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //create
    @PostMapping("")
    public ResponseEntity<?> createComment(HttpServletRequest request, @RequestBody Comment comment) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Comment newComment = commentService.createComment(comment, userId);
        simpMessagingTemplate.convertAndSend("/topic/posts/" + newComment.getPostId() + "/comment", newComment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    //update
    //update comment
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable String id, @RequestBody Comment comment) throws Exception {
        Comment updateComment = commentService.updateComment(id, comment);
        simpMessagingTemplate.convertAndSend("/topic/posts/" + updateComment.getPostId() + "/updatecomment", updateComment);
        return new ResponseEntity<>(updateComment, HttpStatus.OK);
    }

    //update comment like
    @PatchMapping("{id}/like")
    public ResponseEntity<?> updateLikeComment(@PathVariable String id, HttpServletRequest request) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Comment comment1 = commentService.updateLike(id, userId);
        simpMessagingTemplate.convertAndSend("/topic/likecomment", comment1);

        return new ResponseEntity<>(comment1, HttpStatus.OK);
    }

    //update comment like
    @PatchMapping("{id}/unlike")
    public ResponseEntity<?> updateUnlikeComment(@PathVariable String id, HttpServletRequest request) throws Exception {
        String jwtToken = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(jwtToken);
        Comment comment1 = commentService.updateUnlike(id, userId);
        simpMessagingTemplate.convertAndSend("/topic/unlikecomment", comment1);
        return new ResponseEntity<>(comment1, HttpStatus.OK);
    }

    //delete comment
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id) throws Exception {
        commentService.deleteComment(id);
        simpMessagingTemplate.convertAndSend("/topic/deletecomment", id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
