package nodv.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "bookmarks")
public class Bookmark {
    @Id
    private String id;
    private String userId;
//    private String postId;
    private List<String> postIds;

    private List<Post> posts;
}
