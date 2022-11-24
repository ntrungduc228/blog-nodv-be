package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Post;
import nodv.repository.PostRepository;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    } // mongodb-method

    public Optional<Post> findById(String id) throws Exception {

        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new Exception("Post is not found");
        }
//        Optional<User> user = userRepository.findById(post.get().getUserId());
//        user.ifPresent(value -> post.get().setUser(value));

        return post;
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(String id, Post post) throws Exception {
        Optional<Post> updatePost = findById(id);
        if (updatePost.isEmpty()) {
            throw new Exception("post not found");
        }
        updatePost.get().setTitle(post.getTitle());
        updatePost.get().setContent(post.getContent());
        updatePost.get().setThumbnail(post.getThumbnail());
        return postRepository.save(updatePost.get());
    }

    public void deletePost(String id) throws Exception {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
        } else {
            throw new NotFoundException("Post not found");
        }
    }

}