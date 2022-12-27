package nodv.repository;

import nodv.controller.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    Page<Post> findByIsPublishIsTrue(Pageable pageable);

    @Query(value = "{'user.id': ?0}", fields = "{ 'content' : 0}")
    List<Post> findByUserId(String userId);

    @Query(fields = "{ 'content' : 0")
    List<Post> findByUserIdAndIsPublish(String userId, Boolean isPublish);

    @Query(fields = "{'topics.slug': ?0}")
    Page<Post> findByTopicsSlug(String topic, Pageable pageable);
}

//db -> repository -> service -> controller