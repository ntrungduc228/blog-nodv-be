package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Post;
import nodv.model.User;
import nodv.repository.PostRepository;
import nodv.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    public List<Post> findAll() {
        return postRepository.findAll();
    } // mongodb-method

    public Post findById(String id) throws Exception {

        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new Exception("Post is not found");
        }
        return post.get();
    }

    public Post createPost(Post post, HttpServletRequest request) {
        String userId = tokenProvider.getUserIdFromToken(tokenProvider.getJwtFromRequest(request));
        User user = userService.findById(userId);
        post.setUserId(user.getId());
        post.setUser(user);
        return postRepository.save(post);
    }

    public Post updatePost(String id, Post post) throws Exception {
        Post updatePost = findById(id);
        updatePost.setTitle(post.getTitle());
        updatePost.setContent(post.getContent());
        updatePost.setThumbnail(post.getThumbnail());
        return postRepository.save(updatePost);
    }

    public void deletePost(String id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
        } else {
            throw new NotFoundException("Post not found");
        }
    }

}