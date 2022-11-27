package nodv.service;

import nodv.exception.ForbiddenException;
import nodv.exception.NotFoundException;
import nodv.model.Post;
import nodv.model.User;
import nodv.repository.PostRepository;
import nodv.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // mongodb-method
    public Post findById(String id) {

        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Post is not found");
        }
        return post.get();
    }

    public Post createPost(Post post, String userId) {
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

    public void deletePost(String id, String userId) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            if (post.get().getUser().getId().equals(userId))
                postRepository.deleteById(id);
            else throw new ForbiddenException("You do not have permission to delete this post");
        } else {
            throw new NotFoundException("Post not found");
        }
    }

    public void changePublish(String id, String userId, boolean isPublic) {
        Post post = findById(id);
        if (!post.getUser().getId().equals(userId))
            throw new ForbiddenException("You do not have permission to update this post");
        post.setIsPublish(isPublic);
        postRepository.save(post);
    }

    public Page<Post> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return postRepository.findByIsPublishIsTrue(pageable);
    }


    public List<Post> findOwnedPost(String userId, String isPublish) {


        if (isPublish == null) return postRepository.findByUserId(userId);
        else return postRepository.findByUserIdAndIsPublish(userId, Boolean.valueOf(isPublish));
    }


}