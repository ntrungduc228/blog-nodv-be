package nodv.repository;

import nodv.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(sort = "{ username : 1 }", fields = "{role : 0}")
    Page<User> findByUsernameLikeIgnoreCase(String name, Pageable pageable);
}