package com.restaurant_rest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final HandlerMappingIntrospector introspector;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        mvc().pattern("/api/v1/auth/**"))
                                .permitAll()
                                .requestMatchers(
                                        mvc().pattern("/"),
                                        mvc().pattern("/v2/api-docs"),
                                        mvc().pattern("/v3/api-docs"),
                                        mvc().pattern("/v3/api-docs/**"),
                                        mvc().pattern("/swagger-resources"),
                                        mvc().pattern("/swagger-resources/**"),
                                        mvc().pattern("/configuration/ui"),
                                        mvc().pattern("/configuration/security"),
                                        mvc().pattern("/swagger-ui/**"),
                                        mvc().pattern("/webjars/**"))
                                .permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc() {
        return new MvcRequestMatcher.Builder(introspector);
    }

}
