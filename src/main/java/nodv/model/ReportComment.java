package nodv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reportComment")
public class ReportComment extends AuditMetadata{
    @Id
    private String id;
    private List<String> userIdReport;
    private String commentId;
    private String contentComment;
    private List<Integer> type;
    private  Integer count;
    private Boolean status;
    @DBRef
    private List<User> user;
}