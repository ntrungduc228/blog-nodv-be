package nodv.security;

import nodv.model.AuthProvider;
import nodv.model.Role;
import nodv.model.User;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsServiceMobile implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String providerId) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByProviderId(providerId);

        if (user.isEmpty()) {
            return UserPrincipal.notifyNewUser();
//            throw new UsernameNotFoundException("user not found mobile");
        }
        return UserPrincipal.create(user.get());

    }

//    private User registerNewUser() {
//        User user = new User();
//        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
//        user.setProviderId(oAuth2UserInfo.getId());
//        user.setUsername(oAuth2UserInfo.getName());
//        user.setEmail(oAuth2UserInfo.getEmail());
//        user.setAvatar(oAuth2UserInfo.getImageUrl());
//        user.setRole(Role.USER);
//        return userRepository.save(user);
//    }
//    }
}