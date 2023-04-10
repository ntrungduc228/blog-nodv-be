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
@Document(collection = "reporting")
public class Reporting extends AuditMetadata {
    @Id
    private String id;
    private List<String> userIds;
    private String content;
    private Boolean isResolved;
    private ReportType type;
    private String objectId;
    @DBRef
    private List<User> users;
}