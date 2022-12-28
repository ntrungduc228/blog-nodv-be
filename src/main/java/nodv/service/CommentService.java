package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Comment;
import nodv.model.Post;
import nodv.model.User;
import nodv.repository.CommentRepository;
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
    public Comment createComment(Comment comment, String userId) throws Exception {
        User user = userService.findById(userId);
        comment.setUser(user);
        comment.setUserId(userId);
        Post post = postService.findById(comment.getPostId(), userId);
        List<String> userLikeIds = new ArrayList<>();
        comment.setUserLikeIds(userLikeIds);
        return commentRepository.save(comment);
    }

    //update
    //update comment
    public Comment updateComment(String id, Comment comment) throws Exception {
        Optional<Comment> updateComment = commentRepository.findById(id);
        if (updateComment.isEmpty()) {
            throw new Exception("comment not found");
        }
        updateComment.get().setContent(comment.getContent());
        return commentRepository.save(updateComment.get());

    }

    //update like
    public Comment updateLike(String id, String userId) throws Exception {
        Optional<Comment> updateLikeComment = commentRepository.findById(id);
        if (updateLikeComment.isEmpty()) {
            throw new Exception("comment not found");
        }
        List<String> temp = updateLikeComment.get().getUserLikeIds();
        if (!temp.contains(userId)) {
            temp.add(userId);
        }

        updateLikeComment.get().setUserLikeIds(temp);

        return commentRepository.save(updateLikeComment.get());
    }

    //update unlike
    public Comment updateUnlike(String id, String userId) throws Exception {
        Optional<Comment> updateUnlike = commentRepository.findById(id);
        if (updateUnlike.isEmpty()) {
            throw new Exception("comment not found");
        }
        List<String> temp = updateUnlike.get().getUserLikeIds();
        temp.remove(userId);
        updateUnlike.get().setUserLikeIds(temp);

        return commentRepository.save(updateUnlike.get());
    }

    //get comment
    public List<Comment> findByPostId(String postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedDateDesc(postId);
        return comments;
    }

    //Delete comment
    public void deleteComment(String id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isPresent()) {
            commentRepository.deleteById(id);
            List<Comment> commentChildren = commentRepository.findByReplyId(id);
            if (commentChildren.size() != 0) {
                commentRepository.deleteByReplyId(id);
            }
        } else {
            throw new NotFoundException("Comment not found");
        }

    }
}
