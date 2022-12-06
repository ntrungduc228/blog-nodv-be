package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Topic;
import nodv.model.User;
import nodv.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TopicService {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserService userService;


    public List<Topic> checkAndCreateListTopic(List<Topic> topics) {
        System.out.println(topics);
        List<Topic> topicsResult = new ArrayList<>();
        Optional<Topic> optionalTopic = Optional.empty();
        for (Topic topic : topics) {
            if (topic.getId() == null) {
                Topic newTopic = new Topic(topic.getName());
                boolean isExist = topicsResult.stream().anyMatch(t -> t.getSlug().equals(newTopic.getSlug()));
                if (isExist) break;
                optionalTopic = topicRepository.findBySlug(newTopic.getSlug());
                if (optionalTopic.isPresent()) {
                    topicsResult.add(optionalTopic.get()); // if present add
                } else topicsResult.add(topicRepository.save(newTopic)); // else create new and add
            } else topicsResult.add(topic);
        }
        return topicsResult;
    }

    public List<Topic> searchByName(String name) {
        return topicRepository.findByNameLikeIgnoreCase(name);
    }

    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    public List<Topic> findUserTopics(String userId) {
        User user = userService.findById(userId);
        return topicRepository.findByIdIn(user.getTopics());
    }

    public Topic findBySlug(String slug) {
        Optional<Topic> topic = topicRepository.findBySlug(slug);
        if (topic.isEmpty()) throw new NotFoundException("Topic not found");
        return topic.get();
    }

    public List<Topic> getRecommendTopicForUser(String userId){
        User user = userService.findById(userId);
        List<Topic> myRecommendTopic =topicRepository.findByIdNotContaining(user.getTopics());
        Random rand = new Random();
        List<Topic> ramdomTopics = new ArrayList<>();

        if(user.getTopics() == null){
            for(int i = 0; i < 10; i++){
                int ramdomIndex = rand.nextInt(myRecommendTopic.size());
                ramdomTopics.add(myRecommendTopic.get(ramdomIndex));
            }
            return ramdomTopics;
        }

        for(int i = 0; i < 10; i++){
            int ramdomIndex = rand.nextInt(myRecommendTopic.size());
            ramdomTopics.add(myRecommendTopic.get(ramdomIndex));
        }
        return ramdomTopics;
    }
}
