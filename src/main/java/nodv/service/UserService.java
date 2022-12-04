package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.User;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

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
        userUpdate.setAvatar(user.getAvatar());
        userUpdate.setUsername(user.getUsername());
        userUpdate.setBio(user.getBio());
        userUpdate.setGender(user.getGender());
        return userRepository.save(userUpdate);
    }

    public Page<User> search(String name, int page, int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findByUsernameLikeIgnoreCase(name, pageable);
    }

    public User setTopics(User user, String userId) {
        User userUpdate = findById(userId);
        userUpdate.setTopics(user.getTopics());
        userRepository.save(userUpdate);
        return userUpdate;
    }
}
