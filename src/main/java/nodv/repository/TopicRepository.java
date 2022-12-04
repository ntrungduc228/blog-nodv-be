package nodv.repository;

import nodv.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends MongoRepository<Topic, String> {
    Optional<Topic> findBySlug(String slug);

    List<Topic> findByName(String name);

    List<Topic> findByNameLikeIgnoreCase(String name);

    List<Topic> findByIdIn(List<String> topics);
}
