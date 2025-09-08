package org.learnspring.expensetracker.config;

import org.learnspring.expensetracker.Service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
   private MyUserDetailService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(customizer -> customizer.disable()).
                cors(Customizer.withDefaults()).
                authorizeHttpRequests(request -> request
                        .requestMatchers("register","login")
                        .permitAll()
                        .anyRequest().authenticated()).
                httpBasic(h -> h.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })).
                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).build();


    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails user1 = User
//                .withDefaultPasswordEncoder()
//                .username("user1")
//                .password("user")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user1);
//    }
}