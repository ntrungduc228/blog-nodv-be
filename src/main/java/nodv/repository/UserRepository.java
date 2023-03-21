package nodv.repository;

import nodv.model.User;
import nodv.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    List<UserProjection> findByFollowingIdContaining(String userId);

    Page<UserProjection> findByIdIn(List<String> ids, Pageable pageable);

    List<UserProjection> findByFollowerIdContaining(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    @Query(sort = "{ username : 1 }", fields = "{role : 0}")
    Page<UserProjection> findByUsernameLikeIgnoreCase(String name, Pageable pageable);

    @Aggregation(pipeline = {"{$match:{'id' : { '$nin' : ?0}}}", "{$sample:{size: ?1}}"})
    List<User> findRandomNotFollowed(List<String> ids, int limit);

    List<User> findByIdNotAndFollowerIdNotContaining(String userId, String Id, Pageable pageable);

    Long countByTopicsContaining(String topicId);
}