package nodv.controller;

import nodv.model.User;
import nodv.payload.AuthRequestMobile;
import nodv.payload.AuthResponse;
import nodv.payload.LoginRequest;
import nodv.repository.UserRepository;
import nodv.security.TokenProvider;
import nodv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://blog-nodv-web.vercel.app"}, allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/mobile/auth-by-mobile")
    public ResponseEntity<?> authenticateByMobile(@RequestBody AuthRequestMobile authRequestMobile){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestMobile.getProvider(),
                        authRequestMobile.getProviderId()
                )
        );

        System.out.println(authentication.getPrincipal());
        String token = "";
        if(authentication.getPrincipal().toString().equalsIgnoreCase("newUserFromMobile")){
            // Register new user here;

            User newUser = userService.registerNewUser(authRequestMobile);
            token = tokenProvider.createNewToken(newUser.getId());

        }else {
            token = tokenProvider.createToken(authentication);
        }

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String token = tokenProvider.getJwtFromRequest(request);
        String userId = tokenProvider.getUserIdFromToken(token);
        return new ResponseEntity<>(userService.findById(userId), HttpStatus.OK);
    }
}
