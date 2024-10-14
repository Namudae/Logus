package com.logus.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.handler.Http401Handler;
import com.logus.common.security.handler.Http403Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private final MemberDetailService userDetailService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ObjectMapper objectMapper;

    // 생성자 주입
    public SecurityConfig(MemberDetailService userDetailService, ObjectMapper objectMapper) {
        this.userDetailService = userDetailService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/home", "/register/**", "/authenticate", "/", "/login", "/error").permitAll(); //인증이 필요 없는 경로
                    registry.requestMatchers("/admin/**").hasRole("ADMIN"); // ADMIN 역할을 가진 사용자만 접근 가능
                    registry.requestMatchers("/user/**").hasRole("USER"); // USER 역할을 가진 사용자만 접근 가능
//                    registry.requestMatchers(HttpMethod.POST, "/posts/**").authenticated(); //인가자만 허용
//                    registry.anyRequest().authenticated();
                    registry.anyRequest().permitAll();
                })
//                .formLogin(httpSecurityFormLoginConfigurer -> {
//                    httpSecurityFormLoginConfigurer
//                            .loginPage("/login")
//                            .successHandler(new AuthenticationSuccessHandler())
//                            .permitAll();
//                })
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .exceptionHandling(e -> {
                    // 권한이 없을 때 커스텀 핸들러로 처리
                    e.accessDeniedHandler(new Http403Handler(objectMapper));
                    // 인증되지 않은 사용자 접근 시 커스텀 핸들러로 처리
                    e.authenticationEntryPoint(new Http401Handler(objectMapper));
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) //before 필터 추가
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
