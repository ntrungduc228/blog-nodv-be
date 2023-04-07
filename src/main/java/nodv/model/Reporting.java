package nodv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reporting")
public class Reporting extends AuditMetadata{
    @Id
    private String id;
    private String userId;
    private String link;
    private String content;
    private String userIsReportedId;
    private Boolean isResolved;
    @DBRef
    private User user;
}