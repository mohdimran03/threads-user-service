package com.example.userservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.userservice.security.jwt.JwtFilter;
import com.example.userservice.security.jwt.JwtUtils;
import com.example.userservice.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class webSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(webSecurityConfig.class);

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService (){
        return new UserDetailsServiceImpl();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{

        logger.info("configuring the web ...");

        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/user/**")
                        .authenticated() // Secure API v1 user endpoints
                        .anyRequest().permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session will be used
                .httpBasic(AbstractHttpConfigurer::disable) // Disable basic authentication
                .formLogin(AbstractHttpConfigurer::disable) // Disable form login
                .addFilterBefore(
                        jwtFilter(),
                        UsernamePasswordAuthenticationFilter.class
                ); // Adding the JWT Filter

        return http.build();
    }

}