package nodv.service;

import nodv.model.Comment;
import nodv.model.Post;
import nodv.model.User;
import nodv.repository.CommentRepository;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    //create
     public Comment createComment(Comment comment,String userId) throws Exception {
         User user = userService.findById(userId);
         comment.setUserId(userId);
         comment.setUser(user);
         Post post = postService.findById(comment.getPostId());
         comment.setPost(post);
         List<String> Userlikeids = new ArrayList<>();
         comment.setUserlikeids(Userlikeids);
         return commentRepository.save(comment);
     }
     //update
    //update comment
     public Comment updateComment(String id,Comment comment) throws Exception {
         Optional<Comment> updateComment = commentRepository.findById(id);
         if(updateComment.isEmpty()) {
             throw new Exception("comment not found");
         }
         updateComment.get().setContent(comment.getContent());
         return commentRepository.save(updateComment.get());

     }
     //update like
    public Comment updatelike(String id,String userId) throws Exception{
         Optional<Comment> updatelikecomment = commentRepository.findById(id);
         if(updatelikecomment.isEmpty()) {
             throw new Exception("comment not found");
         }
         List<String> temp = updatelikecomment.get().getUserlikeids();
//             if(!temp.contains(userId)) {
//               
//             }
        temp.add(userId);
         updatelikecomment.get().setUserlikeids(temp);

         return commentRepository.save(updatelikecomment.get());
    }
    //update unlike
    public Comment updateUnlike(String id,String userId) throws Exception{
        Optional<Comment> updateunlike= commentRepository.findById(id);
        if(updateunlike.isEmpty()) {
            throw new Exception("comment not found");
        }
        List<String> temp = updateunlike.get().getUserlikeids();
        temp.remove(userId);
        updateunlike.get().setUserlikeids(temp);

        return commentRepository.save(updateunlike.get());
    }

}
