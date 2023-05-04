package nodv.controller;

import nodv.model.*;
import nodv.projection.PostPreviewProjection;
import nodv.security.TokenProvider;
import nodv.service.PostService;
import nodv.service.CommentService;
import nodv.service.ReportingService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    @Autowired
    ReportingService reportingService;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // get posts
    @GetMapping("")
    public ResponseEntity<?> getPosts(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "direction", required = false) String sortDirection,
            @RequestParam(value = "user", required = false) String authorId,
            HttpServletRequest request
    ) {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = null;
        if (token != null) {
            userId = tokenProvider.getUserIdFromToken(token);
        }
        Page<Document> postsPage = postService.findByFilter(page, limit, id, topic, title, authorId, sortBy, sortDirection, userId);
        return new ResponseEntity<>(postsPage, HttpStatus.OK);
    }

    @GetMapping("/following")
    public ResponseEntity<?> getPostsByFollowing(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "isFollowing", required = false) Boolean isFollowing,
            HttpServletRequest request
    ) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Page<PostPreviewProjection> posts = postService.findFollowing(page, limit, userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


    @GetMapping("/trending")
    public ResponseEntity<?> getPostsTrending(
            @RequestParam(value = "limit", defaultValue = "6", required = false) int limit) {
        List<Document> posts = postService.findTopByLike(limit);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getPostsByUser(
            @PathVariable String id, @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) {
//        List<Post> posts = postService.findOwnedPost(id, "true");
        Page<PostPreviewProjection> posts = postService.findOwnPosts(page, limit, id, true);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    ;

    @GetMapping("/me")
    public ResponseEntity<?> getOwnedPosts(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "isPublish", required = false) Boolean isPublish,
            HttpServletRequest request
    ) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
//        List<Post> posts = postService.findOwnedPost(userId, isPublish);
        Page<PostPreviewProjection> posts = postService.findOwnPosts(page, limit, userId, isPublish);

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


    // get post by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id, HttpServletRequest request) {

        String userId = null;
        String token = tokenProvider.getJwtFromRequest(request);
        if (token != null) {
            userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        }
        Post post = postService.findById(id, userId);
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
    public ResponseEntity<?> updatePost(@RequestBody Post post,
                                        @PathVariable String id,
                                        HttpServletRequest request) throws Exception {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post newPost = postService.updatePost(id, post, userId);
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
        simpMessagingTemplate.convertAndSend("/topic/posts/" + post.getId() + "/like", post);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PatchMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable String id, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Post post = postService.unlikePost(id, userId);
        simpMessagingTemplate.convertAndSend("/topic/posts/" + post.getId() + "/like", post);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<?> report(HttpServletRequest request, @RequestBody Reporting reporting) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        Reporting report = reportingService.createReport(reporting, userId, ReportType.POST);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    //get comment
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComment(@PathVariable String id) {
        List<Comment> comments = commentService.findByPostId(id);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
