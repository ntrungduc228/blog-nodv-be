package nodv.controller;

import nodv.model.Bookmark;
import nodv.model.Post;
import nodv.payload.BookmarkDTO;
import nodv.projection.PostPreviewProjection;
import nodv.repository.PostRepository;
import nodv.security.TokenProvider;
import nodv.service.BookmarkService;
import nodv.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app", "https://blog-nodv-admin.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    PostService postService;
    @Autowired
    TokenProvider tokenProvider;

    // create bookmark
    @PostMapping("")
    public ResponseEntity<?> createBookmark(HttpServletRequest request, @RequestBody String postId) {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        Bookmark bookmark = bookmarkService.createBookmark(new BookmarkDTO(userId, postId));

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getBookmark(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            HttpServletRequest request) {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        Page<PostPreviewProjection> postsPage = postService.findByUserBookmark(page, limit, userId);
        return new ResponseEntity<>(postsPage, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getListPostIds(HttpServletRequest request) {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        List<String> postIds = bookmarkService.getListPostIds(userId);
        return new ResponseEntity<>(postIds, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> findByUserId(HttpServletRequest request) throws Exception {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        Bookmark bookmark = bookmarkService.findByUserId(userId);
        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePostIdToBookmark(@PathVariable String postId, HttpServletRequest request) throws Exception {
        System.out.println(postId);
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
//        List<String> postIds = bookmarkService.updatePostIdToBookmark(userId, postId);
        Post post = bookmarkService.updatePostIdToBookmark(userId, postId);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }


}