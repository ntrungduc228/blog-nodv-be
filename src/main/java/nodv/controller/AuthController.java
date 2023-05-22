package nodv.controller;

import nodv.exception.BadRequestException;
import nodv.model.User;
import nodv.payload.*;
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
import java.net.MalformedURLException;
import java.net.URL;

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

    @PostMapping("/verify/{id}/{otp}")
    public ResponseEntity<?> verifyAccount(@PathVariable String id, @PathVariable Integer otp){
//        Integer otp = Integer.parseInt(request.getParameter("otp"));
        if(userService.verifyAccountSignUp(id, otp)){
            String token = tokenProvider.createNewToken(id);
            return ResponseEntity.ok(new AuthResponse(token));
        }
        throw new BadRequestException("Bad Request");
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<?> verifyForgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        String id =userService.verifyForgotPassword(forgotPasswordRequest);
        if(id!=null && !id.isEmpty()){
            String token = tokenProvider.createNewToken(id);
            return ResponseEntity.ok(new AuthResponse(token));
        }
        throw new BadRequestException("Bad Request");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> verifyAccount(HttpServletRequest request){
        String email = request.getParameter("email");
       userService.forgotPassword(email);
        return ResponseEntity.ok("Send email "+ email +" successfully !!!");

    }


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

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest signupRequest, HttpServletRequest request) throws MalformedURLException {
        SignUpResponse user = userService.signUp(signupRequest);
        return ResponseEntity.ok(user);
    }

//    public String getURLBase(HttpServletRequest request) throws MalformedURLException {
//
//        URL requestURL = new URL(request.getRequestURL().toString());
//        String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
//        return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
//
//    }

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
        return new ResponseEntity<>(userService.getUserInfo(userId), HttpStatus.OK);
    }
}
