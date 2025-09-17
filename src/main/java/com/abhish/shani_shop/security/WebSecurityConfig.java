package com.abhish.shani_shop.security;

import static com.abhish.shani_shop.enums.RoleType.ADMIN;
import static com.abhish.shani_shop.enums.RoleType.CUSTOMER;
import static com.abhish.shani_shop.enums.RoleType.SELLER;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JWTAuthFilter jwtAuthFilter;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(
                        sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/products/public/**", "/api/v1/users/public/**",
                                "/api/v1/categories/public/**", "/api/v1/images/image/download/**")
                        .permitAll()
                        .requestMatchers("/api/v1/products/seller/**").hasRole(SELLER.name())
                        .requestMatchers("/api/v1/orders/**", "/api/v1/cartItems/**", "/api/v1/carts/**")
                        .hasRole(CUSTOMER.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole(ADMIN.name())
                        .requestMatchers("/api/v1/images/image/delete/**").hasAnyRole(ADMIN.name(), SELLER.name())
                        .requestMatchers("/api/v1/images/**").hasRole(SELLER.name())
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);

                        }));

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
