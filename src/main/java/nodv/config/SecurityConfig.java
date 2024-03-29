package nodv.config;

import nodv.model.Role;
import nodv.security.CustomUserDetailsService;
import nodv.security.RestAuthenticationEntryPoint;
import nodv.security.TokenAuthenticationFilter;
import nodv.security.oauth2.CustomOAuth2UserService;
import nodv.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import nodv.security.oauth2.OAuth2AuthenticationFailureHandler;
import nodv.security.oauth2.OAuth2AuthenticationSuccessHandler;
import nodv.security.provider.CustomAuthenticationProvider;
import nodv.security.provider.MobileAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private MobileAuthenticationProvider mobileAuthenticationProvider;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

//    @Override
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder
//                .userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }

    //     adding our custom authentication provider
//     authentication manager will call this customer provider's
//     authenticate method from now on.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider)
                .authenticationProvider(mobileAuthenticationProvider);
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/users/{email}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/{id}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/user/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts/{id}/comments").permitAll()
                .antMatchers(HttpMethod.GET, "/api/topics/search").permitAll()
                .antMatchers(HttpMethod.GET, "/api/topics/{slug}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/topics/random").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id}/**").permitAll()
                .antMatchers("/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                .permitAll()
                .antMatchers("/api/admin/**").hasRole(String.valueOf(Role.ADMIN))
                .antMatchers("/api/auth/**", "/oauth2/**", "/api/ws/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        // Add our custom Token based authentication filter

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

