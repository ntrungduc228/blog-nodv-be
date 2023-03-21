package nodv.payload;

import lombok.*;
import nodv.model.Topic;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO extends Topic {
    private Long postCounts;
    private Long followerCounts;

    public TopicDTO(String id, String name, String slug, Long postCounts, Long followerCounts) {
        super(id, slug, name);
        this.postCounts = postCounts;
        this.followerCounts = followerCounts;
    }
}
