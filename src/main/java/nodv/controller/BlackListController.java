package nodv.controller;

import nodv.model.BlackList;
import nodv.model.Bookmark;
import nodv.payload.BlackListDTO;
import nodv.security.TokenProvider;
import nodv.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/blackLists")
public class BlackListController {
    @Autowired
    BlackListService blackListService;

    @Autowired
    TokenProvider tokenProvider;

    @PostMapping("")
    public ResponseEntity<?> createBlackList(HttpServletRequest request, @RequestBody String postId){
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        BlackList blackList = blackListService.createBlackList(new BlackListDTO(userId, postId));

        return new ResponseEntity<>(blackList, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getListPostId(HttpServletRequest request) throws Exception{
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        List<String> postIds = blackListService.getListPostIds(userId);

        return  new ResponseEntity<>(postIds, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByUserId(@PathVariable String id, HttpServletRequest request) throws Exception {
//        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        BlackList blackList = blackListService.findByUserId(id);

        return new ResponseEntity<>(blackList, HttpStatus.OK);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePostIdToBlackList(@PathVariable String postId, HttpServletRequest request) throws Exception{
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        List<String> postIds = blackListService.updatePostIdToBlackList(userId, postId);

        return new ResponseEntity<>(postIds, HttpStatus.OK);
    }

}

