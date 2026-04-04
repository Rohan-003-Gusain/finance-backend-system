package com.finance.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.finance.service.impl.UserDetailsServiceImpl;


@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtFilter jwtFilter;
    
    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, JwtFilter jwtFilter) {
    	this.userDetailsServiceImpl = userDetailsServiceImpl;
    	this.jwtFilter = jwtFilter;
    }

    // ========== PASSWORD ENCODER ==========
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // ========== AUTHENTICATION MANAGER ==========
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ========== SECURITY FILTER CHAIN ==========
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
	        .sessionManagement(session -> session
	        	    .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
	        	)
            .authorizeHttpRequests(auth -> auth
            		
            		.requestMatchers(
            		        "/v3/api-docs/**",
            		        "/swagger-ui/**",
            		        "/swagger-ui.html"
            		).permitAll()
            		
                .requestMatchers("/", "/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/transactions/**").hasAnyRole("ADMIN", "ANALYST")
                .requestMatchers("/dashboard/**").permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsServiceImpl)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}