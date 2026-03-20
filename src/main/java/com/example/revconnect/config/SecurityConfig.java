package com.example.revconnect.config;

//import com.example.revconnect.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

//    private JwtAuthenticationFilter jwtFilter;
//    
//    public SecurityConfig(JwtAuthenticationFilter theJwtAuthenticationFilter) {
//    	
//    	this.jwtFilter = theJwtAuthenticationFilter;
//    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                  // trigger on /logout
                .logoutSuccessUrl("/login")            // redirect to /login (no ?logout) ✅
                .invalidateHttpSession(true)           // clear session
                .deleteCookies("JSESSIONID", "jwt")    // clear cookies
            );

        return http.build();
    }
}