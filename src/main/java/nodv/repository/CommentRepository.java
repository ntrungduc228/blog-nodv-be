package nodv.repository;

import nodv.controller.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);

    List<Comment> findByReplyId(String replyId);

    @Override
    void deleteById(String id);

    void deleteByReplyId(String replyId);

    void deleteByPostId(String postId);
}
