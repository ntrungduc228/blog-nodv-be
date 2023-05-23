package nodv.security.provider;

import nodv.exception.BadRequestException;
import nodv.security.CustomUserDetailsService;
import nodv.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    // Injecting available encryption bean
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());
        UserPrincipal userDetails = (UserPrincipal) customUserDetailsService.loadUserByUsername(username);

        // If user is not null, then we check if password matches
        if (userDetails != null){
            if (passwordEncoder.matches(password, userDetails.getPassword())){
                // if it matches, then we can initialize UsernamePasswordAuthenticationToken.
                // Attention! We used its 3 parameters constructor.

                if(!userDetails.getActive() && userDetails.getOtp() > 0){
                    throw new BadRequestException("Please verify your account");
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
                return authenticationToken;
            }else throw new UsernameNotFoundException("Incorrect email or password!!!");

        }
        return null;

//        throw new BadCredentialsException("Error!!");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}