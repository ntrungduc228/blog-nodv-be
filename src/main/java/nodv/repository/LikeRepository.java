package nodv.repository;

import nodv.model.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface LikeRepository extends MongoRepository<Like, String> {
    @Query("{'userId': ?0, postId: ?1}")
    Optional<Like> findLike(String userId, String postId);


}
