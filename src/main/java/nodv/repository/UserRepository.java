package nodv.repository;

import nodv.model.Post;
import nodv.model.Role;
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

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<UserProjection> findByFollowingIdContaining(String userId);
    Optional<User> findByOtp(Integer otp);

    Optional<User> findByIdAndOtp(String id, Integer otp);
    Optional<User> findByOtpAndIsActive(Integer otp, Boolean isActive);


    @Query(value = "{ '_id' : ?0 }", fields = "{  'password': 0}")
    Optional<User> findByIdExcludingPassword(String id);


    Page<UserProjection> findByIdIn(List<String> ids, Pageable pageable);

    @Query(value = "{ '_id' : ?0 }", fields = "{  'provider': 0, 'providerId': 0 }")
    Optional<User> findByIdExcludingProviderAndProviderId(String id);

    List<UserProjection> findByFollowerIdContaining(String userId);

    Optional<User> findByEmail(String email);

    @Query(value = "{ 'role' : ?0 }", fields = "{ 'provider': 0, 'providerId': 0 }")
    List<User> findByRole(Role role);

    Optional<User> findByProviderId(String providerId);

    @Query(sort = "{ username : 1 }", fields = "{role : 0}")
    Page<UserProjection> findByUsernameLikeIgnoreCase(String name, Pageable pageable);

    @Aggregation(pipeline = {"{$match:{'id' : { '$nin' : ?0}}}", "{$sample:{size: ?1}}"})
    List<User> findRandomNotFollowed(List<String> ids, int limit);

    List<User> findByIdNotAndFollowerIdNotContaining(String userId, String Id, Pageable pageable);

    Long countByTopicsContaining(String topicId);

}

