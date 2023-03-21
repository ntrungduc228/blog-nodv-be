package nodv.projection;

import nodv.model.Post;

import java.time.LocalDateTime;
import java.util.List;


public interface PostPreviewProjection {
    String getId();

    String getTitle();

    String getSubtitle();

    LocalDateTime getCreatedDate();

    Integer getTimeRead();

    String getThumbnail();

    Boolean getIsPublish();

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
