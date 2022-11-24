package nodv.security;

import nodv.model.User;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<User> users = userRepository.findByEmail(email);

        if (users.toArray().length != 1) {
            throw new UsernameNotFoundException("user not found");
        }
        User user = users.get(0);
        return UserPrincipal.create(user);
    }


    public User findByEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.toArray().length < 1)
            return null;
        return users.get(0);
    }
}
