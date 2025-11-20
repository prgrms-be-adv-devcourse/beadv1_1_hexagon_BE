package com.example.memberservice.common.security;

import com.example.memberservice.common.security.handler.OAuthLoginFailureHandler;
import com.example.memberservice.common.security.handler.OAuthLoginSuccessHandler;
import com.example.memberservice.common.security.service.CustomOAuth2UserService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("*"));    // 모든 HTTP 메서드 허용
        config.setAllowedHeaders(List.of("*"));    // 모든 헤더 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfoEndpointConfig ->
                    userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(oAuthLoginSuccessHandler)
                .failureHandler(oAuthLoginFailureHandler)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return httpSecurity.build();

    }
}
