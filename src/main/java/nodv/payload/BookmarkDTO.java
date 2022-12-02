package nodv.payload;

import lombok.*;
import nodv.model.Post;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private String userId;

    private String postId;

//    private List<Post> posts;
}