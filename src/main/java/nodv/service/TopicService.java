package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.Topic;
import nodv.model.User;
import nodv.payload.TopicDTO;
import nodv.repository.PostRepository;
import nodv.repository.TopicRepository;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PostRepository postRepository;

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

    public List<Topic> findRecommend(String userId) {
        User user = userService.findById(userId);
        return topicRepository.findRandom(user.getTopics());
    }

    public List<Topic> findRandom() {
        List<String> topics = new ArrayList<>();
        return topicRepository.findRandom(topics);
    }

    public TopicDTO getDetail(String topicSlug) {
        Topic topic = findBySlug(topicSlug);
        String topicId = topic.getId();
        Long postCounts = postRepository.countByTopicsId(topicId);
        Long followerCounts = userRepository.countByTopicsContaining(topicId);
        return new TopicDTO(topicId, topic.getName(), topic.getSlug(), postCounts, followerCounts);
    }
}
