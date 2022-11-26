package nodv.controller;

import nodv.model.Bookmark;
import nodv.model.Post;
import nodv.payload.BookmarkDTO;
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

//    @Autowired
//

    // create bookmark
    @PostMapping("")
    public ResponseEntity<?> createBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        Bookmark bookmark = bookmarkService.createBookmark(bookmarkDTO);

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> findByUserId(@RequestParam String userId, HttpServletRequest request) throws Exception {
        Bookmark bookmark = bookmarkService.findByUserId(userId);

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @PatchMapping("/add")
    public ResponseEntity<?> addPostIdToBookmark(@RequestBody BookmarkDTO bookmarkDTO) throws Exception {
        Bookmark bookmark = bookmarkService.addPostIdToBookmark(bookmarkDTO.getUserId(), bookmarkDTO.getPostId());

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }

    @PatchMapping("/delete")
    public ResponseEntity<?> deletePostIdtoBookmark(@RequestBody BookmarkDTO bookmarkDTO) throws Exception {
        Bookmark bookmark = bookmarkService.deletePostIdToBookmark(bookmarkDTO.getUserId(), bookmarkDTO.getPostId());

        return new ResponseEntity<>(bookmark, HttpStatus.OK);
    }
}