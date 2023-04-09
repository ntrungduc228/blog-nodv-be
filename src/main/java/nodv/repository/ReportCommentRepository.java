package nodv.repository;

import nodv.model.ReportComment;
import nodv.model.Reporting;
import nodv.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReportCommentRepository extends MongoRepository<ReportComment, String> {
    List<ReportComment> findByCommentIdAndType(String commentId, int type);
    ReportComment findByCommentId(String commentId);
}
