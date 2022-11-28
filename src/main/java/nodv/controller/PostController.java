package nodv.controller;

import nodv.model.Post;
import nodv.security.TokenProvider;
import nodv.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/posts") //localhost://8081/api/posts    - method:....
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    TokenProvider tokenProvider;

    // get posts
    @GetMapping("")
    public ResponseEntity<?> getPosts(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit
    ) {
        Page<Post> posts = postService.findAll(page, limit);
        return new ResponseEntity<>(posts.get(), HttpStatus.OK);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getPostsByUser() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    ;

    @GetMapping("/me")
    public ResponseEntity<?> getOwnedPosts(
            @RequestParam(value = "isPublish", required = false) String isPublish,
            HttpServletRequest request
    ) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        List<Post> posts = postService.findOwnedPost(userId, isPublish);

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


    // get post by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id) throws Exception {
        Post post = postService.findById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // create post
    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestBody Post post, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post newPost = postService.createPost(post, userId);
        return new ResponseEntity<>(newPost, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@RequestBody Post post, @PathVariable String id) throws Exception {
        Post newPost = postService.updatePost(id, post);
        return new ResponseEntity<>(newPost, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        postService.deletePost(id, userId);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> publishPost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.changePublish(id, userId, true);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PatchMapping("/{id}/unpublished")
    public ResponseEntity<?> unPublishPost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.changePublish(id, userId, false);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.likePost(id, userId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PatchMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.unlikePost(id, userId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
