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
@Document(collection = "posts")
public class Post extends AuditMetadata {
    @Id
    private String id;
    private String title;
    private String subtitle;
    private String content;
    private String thumbnail;
    private String userId;
    private Integer likes;
    private Integer timeRead;
    private List<String> topic;
    private Boolean isPublish;
    private List<String> userLikeIds;
    @DBRef
    private List<Topic> topics;
    @DBRef
    private User user;
}