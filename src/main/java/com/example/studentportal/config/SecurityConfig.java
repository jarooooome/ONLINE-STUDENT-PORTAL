package com.example.studentportal.config;

import com.example.studentportal.service.CustomLoginSuccessHandler;
import com.example.studentportal.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomLoginSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CSRF protection
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/calendar/events/**") // Allow calendar API endpoints
                )

                // Cache and frame options
                .headers(headers -> headers
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                // Session management
                .sessionManagement(session -> session
                        .invalidSessionUrl("/auth/login?invalid-session=true")
                        .maximumSessions(1)
                        .expiredUrl("/auth/login?session-expired=true")
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index",
                                "/auth/**",
                                "/forgot-password",
                                "/verify-otp",       // Added this line
                                "/reset-password",   // Added this line
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/student/**").hasAuthority("ROLE_STUDENT")
                        .requestMatchers("/cashier/**").hasAuthority("ROLE_CASHIER") // ✅ Added cashier role
                        .requestMatchers("/registrar/**").hasAuthority("ROLE_REGISTRAR") // ✅ Added cashier role
                        .requestMatchers("/professor/**").hasAuthority("ROLE_PROFESSOR") // ✅ Added cashier role
                        .requestMatchers("/OSA/**").hasAuthority("ROLE_OSA") // ✅ Added cashier role
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )

                // Form login
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                )

                // User details service
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
