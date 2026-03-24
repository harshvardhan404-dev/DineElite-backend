package com.dineelite.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
                .requestMatchers("/api/register").permitAll()
                .requestMatchers("/api/verify").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/advertisements/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                // Protected
                .requestMatchers("/api/booking/create").authenticated()
                .requestMatchers("/api/booking/history/**").authenticated()
                .requestMatchers("/api/booking/user/**").authenticated()
                .requestMatchers("/api/booking/cancel/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()
                .requestMatchers("/api/table-layout/**").authenticated()
                .requestMatchers("/api/social/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated()

                // ADMIN
                .requestMatchers("/api/booking/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/advertisements/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/menu/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/login")
                .successHandler((request, response, authentication) -> {
                    System.out.println(">>> LOGIN SUCCESS for " + authentication.getName());
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Login successful\"}");
                })
                .failureHandler((request, response, exception) -> {
                    System.out.println(">>> LOGIN FAILED for " + request.getParameter("username") + ": " + exception.getMessage());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Login failed: " + exception.getMessage() + "\"}");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpStatus.OK.value());
                })
                .permitAll()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authenticationProvider(authenticationProvider())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use environment variable for frontend URL, default to localhost:4200
        String frontendUrl = System.getenv("FRONTEND_URL");
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            frontendUrl = "http://localhost:4200";
        }
        // Strip trailing slash to prevent origin mismatch
        if (frontendUrl.endsWith("/")) {
            frontendUrl = frontendUrl.substring(0, frontendUrl.length() - 1);
        }
        
        List<String> allowedOrigins = new java.util.ArrayList<>();
        allowedOrigins.add("http://localhost:4200");
        allowedOrigins.add("https://dineelite.netlify.app");
        allowedOrigins.add("https://admirable-tarsier-c00d50.netlify.app");
        allowedOrigins.add("https://dineelite-backend.onrender.com");
        
        if (frontendUrl != null && !frontendUrl.isEmpty()) {
            allowedOrigins.add(frontendUrl);
        }
        
        configuration.setAllowedOrigins(allowedOrigins);
        System.out.println(">>> CORS: Allowed Origins: " + allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
