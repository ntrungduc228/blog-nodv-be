package nodv.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(value="comments")
public class Comment {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String content;
    private Integer likes;
    private String replyId;
}