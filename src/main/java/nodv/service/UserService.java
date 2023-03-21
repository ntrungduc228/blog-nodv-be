package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.AuthProvider;
import nodv.model.Role;
import nodv.model.User;
import nodv.payload.AuthRequestMobile;
import nodv.projection.UserProjection;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public User registerNewUser(AuthRequestMobile authRequestMobile) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(authRequestMobile.getProvider()));
        user.setProviderId(authRequestMobile.getProviderId());
        user.setUsername(authRequestMobile.getUsername());
        user.setEmail(authRequestMobile.getEmail());
        user.setAvatar(authRequestMobile.getAvatar());
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public User findByEmail(String email) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return user.get();

    }

    public User findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new NotFoundException("User not found");

        return user.get();
    }

    public User updateBasicProfile(User user, String id) {
        User userUpdate = findById(id);
        if (!user.getUsername().isEmpty()) {
            userUpdate.setUsername(user.getUsername());
        }
        userUpdate.setAvatar(user.getAvatar());
        userUpdate.setBio(user.getBio());
        userUpdate.setGender(user.getGender());
        return userRepository.save(userUpdate);
    }

    public Page<UserProjection> search(String name, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findByUsernameLikeIgnoreCase(name, pageable);
    }


    public List<User> getUsesNotFollowed(String userId, int limit) {
        User user = findById(userId);
        List<String> userIdsIgnore = new ArrayList<>(); // list skip user
        userIdsIgnore.add(userId); // add skip user(sender) send request
        if (user.getFollowingId() != null) userIdsIgnore.addAll(user.getFollowingId()); // check null list
        return userRepository.findRandomNotFollowed(userIdsIgnore, limit);
    }


    public User followUser(String userId, String followId) {
        Optional<User> user = userRepository.findById(followId);
        if (user.isEmpty())
            throw new NotFoundException("User not found");

        User checkDuplicate = findById(userId);
        List<String> arrCheck = checkDuplicate.getFollowingId();
        if (arrCheck != null && arrCheck.contains(followId)) {
            throw new NotFoundException("Followed");
        }

        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(userId);
        query.addCriteria(criteria);

        Query query2 = new Query();
        Criteria criteria2 = Criteria.where("id").is(followId);
        query2.addCriteria(criteria2);

        Update update = new Update();
        update.push("followingId", followId);
        mongoTemplate.updateFirst(query, update, User.class);

        Update update2 = new Update();
        update2.push("followerId", userId);
        mongoTemplate.updateFirst(query2, update2, User.class);

        return findById(followId);
    }

    public User unFollowUser(String userId, String unFollowId) {
        Optional<User> user = userRepository.findById(unFollowId);
        if (user.isEmpty())
            throw new NotFoundException("User not found");
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(userId);
        query.addCriteria(criteria);


        Query query2 = new Query();
        Criteria criteria2 = Criteria.where("id").is(unFollowId);
        query2.addCriteria(criteria2);

        Update update = new Update();
        update.pull("followingId", unFollowId);
        mongoTemplate.updateFirst(query, update, User.class);


        Update update2 = new Update();
        update2.pull("followerId", userId);
        mongoTemplate.updateFirst(query2, update2, User.class);

        return findById(unFollowId);
    }

    public User setTopics(User user, String userId) {
        User userUpdate = findById(userId);
        userUpdate.setTopics(user.getTopics());
        return userRepository.save(userUpdate);
    }

    public User updateCountNotifications(String userId, String isIncrease) {
        User user = this.findById(userId);

        if (Boolean.parseBoolean(isIncrease)) {
            Integer countNotification = user.getNotificationsCount() != null ? user.getNotificationsCount() + 1 : 1;
            user.setNotificationsCount(countNotification);
        } else user.setNotificationsCount(0);

        //  System.out.println("count notification user service" + user.getNotificationsCount());

        return userRepository.save(user);
    }

    //get user Follower
    public List<UserProjection> getUsersFollower(String userId) {
        return userRepository.findByFollowingIdContaining(userId);

    }

    public Page<UserProjection> getFollowers(int page, int limit, String userId) {
        User user = findById(userId);
        Pageable pageable = PageRequest.of(page, limit, Sort.by("username").descending());
        return userRepository.findByIdIn(user.getFollowerId(), pageable);
    }

    public Page<UserProjection> getFollowing(int page, int limit, String userId) {
        User user = findById(userId);
        Pageable pageable = PageRequest.of(page, limit, Sort.by("username").descending());
        return userRepository.findByIdIn(user.getFollowingId(), pageable);
    }

    //get user Following
    public List<UserProjection> getUsersFollowing(String userId) {
        return userRepository.findByFollowerIdContaining(userId);

    }

    public User followTopics(String topicId, String userId) {
        User userUpdate = findById(userId);
//        userUpdate.setTopics(user.getTopics());
        List<String> newListTopic = new ArrayList<String>();
        if (userUpdate.getTopics() != null) {
            newListTopic = userUpdate.getTopics();
            if (newListTopic.contains(topicId)) {
                List<String> itemsToRemove = new ArrayList<>();
                itemsToRemove.add(topicId);
                newListTopic.removeAll(itemsToRemove);
            } else {
//                newListTopic = userUpdate.getTopics();
                newListTopic.add(topicId);
            }
        } else {
            newListTopic.add(topicId);
        }

        userUpdate.setTopics(newListTopic);
        return userRepository.save(userUpdate);
    }
}

