package nodv.projection;

import nodv.model.Post;

import java.time.LocalDateTime;
import java.util.List;


public interface PostPreviewProjection {
    String getId();

    String getTitle();

    String getSubtitle();

    LocalDateTime getCreatedDate();

    String getTimeRead();

    String getThumbnail();

    UserProjection getUser();

    List<TopicsProjection> getTopics();

    interface UserProjection {
        String getId();

        String getUsername();

        String getAvatar();
    }

    interface TopicsProjection {
        String getId();

        String getName();

        String getSlug();
    }
}
