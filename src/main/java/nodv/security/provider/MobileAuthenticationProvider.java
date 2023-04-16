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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MobileAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private CustomUserDetailsServiceMobile customUserDetailsServiceMobile;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String provider = authentication.getName();
        String providerId = String.valueOf(authentication.getCredentials());
        UserPrincipal userDetails = (UserPrincipal) customUserDetailsServiceMobile.loadUserByUsername(providerId);
//        userDetails.getAuthorities().forEach((item) -> {
//            System.out.println("item " + item);
//        });

        if(validateEmail(provider)){
            // sign in by email vs password
            return null;
        }

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