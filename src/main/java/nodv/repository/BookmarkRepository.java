package nodv.repository;

import nodv.controller.model.Bookmark;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface BookmarkRepository extends MongoRepository<Bookmark, String> {
    Optional<Bookmark> findByUserId(String userId);

//    @Update("{ '$push' : { 'postIds' : ?1 } }")
//    void findAndPushPostIdsByUserId(String userId, String postId);
}