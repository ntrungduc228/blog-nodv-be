package nodv.service;

import nodv.model.Comment;
import nodv.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    public List<Comment> findAll() {return commentRepository.findAll();}

    public Comment create(Comment comment) {return commentRepository.save(comment);}

    public Comment update(String id, Comment comment){
        Optional<Comment> _comment = commentRepository.findById(id);
        _comment.get().setLikes(comment.getLikes());
        _comment.get().setContent(comment.getContent());
        _comment.get().setReplyId(comment.getReplyId());

        return commentRepository.save(_comment.get());
    }

    public void delete(String id){
        commentRepository.deleteById(id);
    }
}