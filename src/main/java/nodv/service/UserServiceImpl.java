package nodv.service;

import nodv.model.User;
import nodv.repository.UserRepository;
import nodv.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.toArray().length < 1)
            return null;
        return users.get(0);
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        return UserPrincipal.create(user);

    }
}
