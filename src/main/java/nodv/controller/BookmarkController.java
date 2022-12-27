package nodv.controller;

import nodv.controller.model.Bookmark;
import nodv.payload.BookmarkDTO;
import nodv.security.TokenProvider;
import nodv.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    @Autowired
    BookmarkService bookmarkService;

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
        List<String> postIds = bookmarkService.updatePostIdToBookmark(userId, postId);

        return new ResponseEntity<>(postIds, HttpStatus.OK);
    }


}