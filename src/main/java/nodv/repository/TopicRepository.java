package nodv.repository;

import nodv.model.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends MongoRepository<Topic, String> {
    Optional<Topic> findBySlug(String slug);

    List<Topic> findByName(String name);

    List<Topic> findByNameLikeIgnoreCase(String name);

    List<Topic> findByIdIn(List<String> topics);

   List <Topic> findByIdNotContaining(List<String> topics );


    @Aggregation(pipeline = {"{$match:{'id' : { $nin: ?0}}}", "{$sample:{size:10}}"})
    List<Topic> findRandom(List<String> topics);
}
