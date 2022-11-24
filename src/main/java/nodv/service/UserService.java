package nodv.service;

import nodv.exception.NotFoundException;
import nodv.model.User;
import nodv.repository.UserRepository;
import nodv.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;


    public User findByEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.toArray().length < 1)
            return null;
        return users.get(0);
    }

    public Optional<User> findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        return UserPrincipal.create(user);

    }
}
