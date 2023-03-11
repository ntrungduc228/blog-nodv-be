package nodv.repository;

import nodv.model.Post;
import nodv.projection.PostPreviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    Page<PostPreviewProjection> findByTopicsIdAndIsPublishTrue(String topicId, Pageable pageable);

    Optional<Post> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    Page<PostPreviewProjection> findByUserIdInAndIsPublishTrue(List<String> followingIds, Pageable pageable);

    Page<Post> findByIsPublishTrue(Pageable pageable);

    Page<PostPreviewProjection> findByUserId(String userId, Pageable pageable);

    Page<PostPreviewProjection> findByUserIdAndIsPublish(String userId, Boolean isPublish, Pageable pageable);

    @Query(fields = "{'topics.slug': ?0}")
    Page<Post> findByTopicsSlug(String topic, Pageable pageable);

    Page<PostPreviewProjection> findByTopicsId(String topicId, Pageable pageable);
}

//db -> repository -> service -> controller