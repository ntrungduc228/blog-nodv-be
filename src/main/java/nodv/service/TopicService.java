package nodv.service;

import nodv.model.Topic;
import nodv.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    MongoTemplate mongoTemplate;


    public List<Topic> checkAndCreateListTopic(List<Topic> topics) {
        List<Topic> topicsResult = new ArrayList<>();
        Optional<Topic> optionalTopic = Optional.empty();
        for (Topic topic : topics) {
            if (topic.getId() == null) {
                optionalTopic = topicRepository.findBySlug(topic.getSlug());
                if (optionalTopic.isPresent()) {
                    topicsResult.add(optionalTopic.get());
                } else topicsResult.add(topicRepository.save(topic));
            }
        }
        return topicsResult;
    }

    public List<Topic> searchByName(String name) {
        return topicRepository.findByNameLikeIgnoreCase(name);
    }
}
