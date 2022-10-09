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
@CrossOrigin(origins = "http://localhost:3000" ,allowCredentials = "true")
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostRepository postRepository;
    PostService postService;
    @GetMapping("")
    public ResponseEntity<Object> getAllPosts(){
        try {
           List<Post> list = postService.findAll();
            if (list.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPostById(@PathVariable String id){
        try{
            return new ResponseEntity<>(postService.findById(id), HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println("e " + e);
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("/create-post")
    public ResponseEntity<Post> createPost(@RequestBody Post post){
        try {
//            Post newPost = new Post();
            Post _post = postRepository.save(post);
            return new ResponseEntity<>(_post, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}