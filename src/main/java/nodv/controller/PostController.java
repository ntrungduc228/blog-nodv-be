package nodv.controller;

import nodv.model.Post;
import nodv.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostRepository postRepository;
    @CrossOrigin(origins = "http://localhost:3000" ,allowCredentials = "true")
    @GetMapping("/get-all-posts")
    public ResponseEntity<List<Post>> getAllPosts(){
        try {
            List<Post> posts = new ArrayList<Post>();
            postRepository.findAll().forEach(posts::add);

            if (posts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-post/{id}")
    public ResponseEntity<Post> getPostById(String Id){
        Optional<Post> post = postRepository.findById(Id);

        if (post.isPresent()) {
            return new ResponseEntity<>(post.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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