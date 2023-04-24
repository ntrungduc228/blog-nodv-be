package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.*;
import nodv.repository.CommentRepository;
import nodv.repository.ReportCommentRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReportCommentRepository reportCommentRepository;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    private MongoTemplate mongoTemplate;

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

    public Comment findById(String id){
        Optional<Comment> comment = commentRepository.findById(id);
        if(!comment.isPresent()) {
            throw new NotFoundException("Comment is not found");
        }
        return comment.get();
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

    public List<ReportComment> findAllReportComment() {
        return reportCommentRepository.findAll();
    }
    public void updateStatusComment(String id, String status) {
        Optional<Comment> updateComment = commentRepository.findById(id);
        if (updateComment.isEmpty()) {
            throw new NotFoundException("comment not found");
        }
        updateComment.get().setStatus(status);
        commentRepository.save(updateComment.get());
    }

    public ReportComment createReportComment(String userId, String id, int type) {
//        List<ReportComment> countReport = reportCommentRepository.findByCommentIdAndType(id, 1);
//        if(!countReport.isEmpty() && countReport != null && countReport.size() > 5){
//            deleteComment(id);
//
//            throw new NotFoundException("Bi bao cao qua lan !!");
//        }
        Optional<Comment> updateComment = commentRepository.findById(id);
        if (updateComment.isEmpty()) {
            throw new NotFoundException("comment not found");
        }
        updateComment.get().setStatus("Reported");
        commentRepository.save(updateComment.get());
//        updateStatusComment(id, "Reported");
        List<String> listUserIdReported = new ArrayList<String>();
        List<Integer> listTypeReported = new ArrayList<Integer>();
        List<User> listUserReported = new ArrayList<User>();
        User user = userService.findById(userId);
        ReportComment reportComment = new ReportComment();

        Optional<Comment> comment = commentRepository.findById(id);
        ReportComment checkExist = reportCommentRepository.findByCommentId(id);
        if (checkExist != null){
            if(countValue(checkExist.getType(), 1) > 4){
                checkExist.setStatus(true);
                reportCommentRepository.save(checkExist);
                deleteComment(id);
                throw new NotFoundException("Bi bao cao qua 5 lan !!");
            }else {
                listUserIdReported = checkExist.getUserIdReport();
                listUserIdReported.add(userId);
                listTypeReported = checkExist.getType();
                listTypeReported.add(type);
                listUserReported = checkExist.getUser();
                listUserReported.add(user);
                checkExist.setType(listTypeReported);
                checkExist.setUserIdReport(listUserIdReported);
                checkExist.setUser(listUserReported);
                checkExist.setCount(checkExist.getCount()+1);
                reportComment = checkExist;
            }
        }else {
            listUserIdReported.add(userId);
            listTypeReported.add(type);
            listUserReported.add(user);
            reportComment.setUserIdReport(listUserIdReported);
            reportComment.setCommentId(id);
            reportComment.setType(listTypeReported);
            reportComment.setCount(1);
            reportComment.setStatus(false);
            reportComment.setUser(listUserReported);
            reportComment.setContentComment(comment.get().getContent());
        }

        return reportCommentRepository.save(reportComment);
//        return countReport;
    }
    public ReportComment findByIdAndType(String id){
        int type = 1;
        Optional<ReportComment> listReport = reportCommentRepository.findById(id);
        return listReport.get();
    }
    public long countValue(List<Integer> list, int value) {
        return list.stream().filter(num -> num == value).count();
    }
    public ReportComment updateReportComment(String id){
        Optional<ReportComment> reportComment = reportCommentRepository.findById(id);
        reportComment.get().setStatus(true);
        deleteComment(reportComment.get().getCommentId());
        return reportCommentRepository.save(reportComment.get());
    }
    public Page<Comment> findByFilter(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return commentRepository.findAll(pageable);
    }
}
