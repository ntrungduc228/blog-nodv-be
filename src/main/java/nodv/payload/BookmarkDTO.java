package nodv.payload;

import lombok.*;

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