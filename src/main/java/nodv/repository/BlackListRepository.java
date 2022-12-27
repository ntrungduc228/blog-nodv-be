package nodv.repository;

import nodv.controller.model.BlackList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlackListRepository extends MongoRepository<BlackList, String> {
    Optional<BlackList> findByUserId(String userId);
}