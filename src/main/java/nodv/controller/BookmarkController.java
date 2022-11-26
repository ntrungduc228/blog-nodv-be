package nodv.controller;

import nodv.model.Bookmark;
import nodv.model.Post;
import nodv.payload.BookmarkDTO;
import nodv.security.TokenProvider;
import nodv.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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

    @GetMapping("/user")
    public ResponseEntity<?> findByUserId(HttpServletRequest request) throws Exception {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        Bookmark bookmark = bookmarkService.findByUserId(userId);

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @PatchMapping("/add")
    public ResponseEntity<?> addPostIdToBookmark(@RequestBody String postId, HttpServletRequest request)  throws Exception {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        Bookmark bookmark = bookmarkService.addPostIdToBookmark(userId, postId);

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @PatchMapping("/delete")
    public ResponseEntity<?> deletePostIdToBookmark(@RequestBody String postId, HttpServletRequest request) throws Exception {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        Bookmark bookmark = bookmarkService.deletePostIdToBookmark(userId, postId);

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }
}