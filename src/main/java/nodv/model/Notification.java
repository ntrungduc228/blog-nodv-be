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
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "notifications")
public class Notification extends AuditMetadata{
    @Id
    private String id;
    private String link;
    private String senderId;
    private String receiverId;
    private String type;
    private Boolean isRead;
     @DBRef
    private User receiver;
     @DBRef
    private User sender;
}

