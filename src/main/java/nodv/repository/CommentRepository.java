package nodv.repository;

import nodv.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostIdOrderByCreatedDateDesc(String postId);
    List<Comment> findByReplyId(String replyId);
    @Override
    void deleteById(String id);
    void deleteByReplyId(String replyId);
    void deleteByPostId(String postId);
    Page<Comment> findAll(Pageable pageable);
}
