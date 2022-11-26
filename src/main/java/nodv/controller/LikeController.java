package nodv.controller;

import nodv.model.Like;
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

    @GetMapping("/getAll")
    public ResponseEntity<?> getLikes() {
        List<Like> likes = likeService.findAllLike();
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @PostMapping("/likePost/{idPost}")
    public ResponseEntity<?> createLike(@PathVariable String idPost, HttpServletRequest request) throws Exception {
        Like newLike = likeService.createLike(idPost, request);
        return new ResponseEntity<>(newLike, HttpStatus.OK);
    }

    @DeleteMapping("/deleteLike/{idPost}")
    public ResponseEntity<?> deleteLike(@PathVariable String idPost, HttpServletRequest request) throws Exception {
        likeService.deleteLike(idPost, request);
        return new ResponseEntity<>(idPost, HttpStatus.OK);
    }

}
