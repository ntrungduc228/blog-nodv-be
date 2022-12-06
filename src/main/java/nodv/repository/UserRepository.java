package nodv.repository;

import nodv.model.Topic;
import nodv.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(sort = "{ username : 1 }", fields = "{role : 0}")
    Page<User> findByUsernameLikeIgnoreCase(String name, Pageable pageable);

    @Aggregation(pipeline = {"{$match:{'id' : { '$nin' : ?0}}}", "{$sample:{size: ?1}}"})
    List<User> findRandomNotFollowed(List<String> ids, int limit);

}