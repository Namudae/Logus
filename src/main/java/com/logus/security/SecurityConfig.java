package com.logus.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 이 클래스가 Spring의 설정 클래스를 나타냄
@EnableWebSecurity // Spring Security를 활성화하는 어노테이션
@EnableMethodSecurity // 메서드 수준의 보안 활성화
@AllArgsConstructor // 모든 필드를 포함하는 생성자를 자동 생성
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService; // 사용자 세부 정보 서비스
    private final AuthEntryPointJwt unauthorizedHandler; // 인증 실패 시 처리하는 핸들러

    @Bean // Spring 컨테이너에 빈으로 등록
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(); // JWT 필터를 빈으로 등록
    }

    @Bean // Spring 컨테이너에 빈으로 등록
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // DAO 인증 제공자 생성
        authProvider.setUserDetailsService(userDetailsService); // 사용자 세부 정보 서비스 설정
        authProvider.setPasswordEncoder(passwordEncoder()); // 비밀번호 인코더 설정
        return authProvider; // 인증 제공자 반환
    }

    @Bean // Spring 컨테이너에 빈으로 등록
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); // 인증 관리자 반환
    }

    @Bean // Spring 컨테이너에 빈으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 비밀번호 인코더 반환
    }

    @Bean // Spring 컨테이너에 빈으로 등록
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // 인증 실패 시 핸들러 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 상태 비저장 세션 관리 설정
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // 인증 없이 접근 허용
                                .requestMatchers("/api/test/**").permitAll() // 인증 없이 접근 허용
                                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                );

        http.authenticationProvider(authenticationProvider()); // 인증 제공자 설정

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가

        return http.build(); // SecurityFilterChain 빌드 및 반환
    }
}
