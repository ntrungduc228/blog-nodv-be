package nodv.security.provider;

import nodv.model.User;
import nodv.security.CustomUserDetailsServiceMobile;
import nodv.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class MobileAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private CustomUserDetailsServiceMobile customUserDetailsServiceMobile;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String provider = authentication.getName();
        String providerId = String.valueOf(authentication.getCredentials());
        UserPrincipal userDetails = (UserPrincipal) customUserDetailsServiceMobile.loadUserByUsername(providerId);
        userDetails.getAuthorities().forEach((item) -> {
            System.out.println("item " + item);
        });

        // If user is not null, then we check if password matches
        if (userDetails != null){
            if(userDetails.getIsNewUser()){
                // new User;
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken("newUserFromMobile", providerId, userDetails.getAuthorities());
                return authenticationToken;
            }

            if(provider.equalsIgnoreCase(String.valueOf(userDetails.getProvider()))){
                // if it matches, then we can initialize UsernamePasswordAuthenticationToken.
               // Attention! We used its 3 parameters constructor.
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, providerId, userDetails.getAuthorities());
                return authenticationToken;
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}