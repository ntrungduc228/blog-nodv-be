package nodv.service;

import nodv.model.Post;
import nodv.model.User;
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
    }

    public Optional<Post> findById(String id) throws Exception {
        Optional<Post> post = postRepository.findById(id);

        Optional<User> user = userRepository.findById(post.get().getUserId());
        post.get().setUser(user.get());
//        if(!post.isPresent()){
//            throw new Exception("Post is not found");
//        }
        return post;

    }

    public Post createNew(Post post){
        return postRepository.save(post);
    }

}