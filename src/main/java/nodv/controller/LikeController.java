package nodv.controller;

import nodv.model.Like;
import nodv.security.TokenProvider;
import nodv.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired
    LikeService likeService;
    @Autowired
    TokenProvider tokenProvider;
    @GetMapping("")
    public ResponseEntity<?> getLikes() {
        List<Like> likes = likeService.findAll();
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> createLike(@PathVariable String postId, HttpServletRequest request) throws Exception {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Like newLike = likeService.createLike(postId, userId);
        return new ResponseEntity<>(newLike, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteLike(@PathVariable String postId, HttpServletRequest request) throws Exception {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        likeService.deleteLike(postId, userId);
        return new ResponseEntity<>(postId, HttpStatus.OK);
    }

}
