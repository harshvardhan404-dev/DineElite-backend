package com.dineelite.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers("/api/restaurants/**").permitAll()
                .requestMatchers("/api/booking/availability").permitAll()
                .requestMatchers("/api/booking/available-slots").permitAll()
                .requestMatchers("/api/booking/create").permitAll()
                .requestMatchers("/api/booking/history/**").permitAll()
                .requestMatchers("/api/booking/user/**").permitAll()
                .requestMatchers("/api/booking/cancel/**").permitAll()
                .requestMatchers("/api/social/**").permitAll()
                .requestMatchers("/api/notifications/**").permitAll()
                .requestMatchers("/api/table-layout/**").permitAll()
                .requestMatchers("/api/register").permitAll()
                .requestMatchers("/api/verify").permitAll()

                // ADMIN endpoints
                .requestMatchers("/api/booking/admin/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/advertisements/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/advertisements").permitAll()
                .requestMatchers("/api/menu/**").permitAll()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Frontend route
                .loginProcessingUrl("/api/login")
                .defaultSuccessUrl("/api/restaurants", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .permitAll()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // plain text (dev only)
    }
}
