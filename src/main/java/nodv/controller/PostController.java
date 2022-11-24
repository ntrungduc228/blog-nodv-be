package nodv.controller;

import nodv.model.Post;
import nodv.repository.PostRepository;
import nodv.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/posts") //localhost://8081/api/posts    - method:....
public class PostController {
    @Autowired
    PostService postService;

    // get posts
    @GetMapping("")
    public ResponseEntity<?> getPosts() {
        List<Post> post = postService.findAll();
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // get post by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id) throws Exception {
        System.out.println(id);
        Optional<Post> post = postService.findById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // create post
    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        Post newPost = postService.createPost(post);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@RequestBody Post post, @PathVariable String id) throws Exception {
        Post newPost = postService.updatePost(id, post);
        return new ResponseEntity<>(newPost, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) throws Exception {
        postService.deletePost(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id) throws Exception {
        postService.deletePost(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
