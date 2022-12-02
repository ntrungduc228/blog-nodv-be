package nodv.repository;

import nodv.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);

    Page<Post> findByIsPublishIsTrue(Pageable pageable);

    @Query(value = "{'user.id': ?0}")
    List<Post> findByUserId(String userId);

    List<Post> findByUserIdAndIsPublish(String userId, Boolean isPublish);

}

//db -> repository -> service -> controller