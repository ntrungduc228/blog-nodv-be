package nodv.controller.model;


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
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "comments")
public class Comment extends AuditMetadata {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String content;
    private List<String> userLikeIds;
    private String replyId;
    @DBRef
    private User user;
}