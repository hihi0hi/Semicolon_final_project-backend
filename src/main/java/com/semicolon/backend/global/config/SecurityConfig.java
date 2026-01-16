package com.semicolon.backend.global.config;

import com.semicolon.backend.global.jwt.JwtCheckFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // << 이거 필수
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtCheckFilter jwtCheckFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/favicon.ico",
                        "/resources/**",
                        "/error",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        log.info("--------필터체인 시작--------");
        httpSecurity
                .csrf(csrf->csrf.disable())
                .cors(Customizer.withDefaults())
                .formLogin(form->form.disable())
                .httpBasic(basic->basic.disable())
                .sessionManagement(session->session.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/community/notice/{id}/view").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/community/gallery/{id}/view").permitAll()
                        .requestMatchers("/ws-chat/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/community/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/member/findId").permitAll()
                        .requestMatchers("/api/auth/sendCode").permitAll()
                        .requestMatchers("/api/auth/checkCode").permitAll()
                        .requestMatchers("/api/auth/sendCodePw").permitAll()
                        .requestMatchers("/api/auth/checkCodePw").permitAll()
                        .requestMatchers("/api/auth/resetPassword").permitAll()
                        .requestMatchers("/api/auth/check/**").permitAll()
                        .requestMatchers("/api/program/**").permitAll()
                        .requestMatchers("/upload/**", "/download/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/upload/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/gallery/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/guide/view/**").permitAll()
                        .requestMatchers("/api/guide/**").permitAll()
                        .requestMatchers("/api/lesson/check/**").permitAll()
                        .requestMatchers("/api/upload/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/community/gallery/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/community/gallery/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/lesson/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/program/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(regexMatcher(".*admin.*")).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/availableSpace/**").permitAll()
                        .requestMatchers("/", "/index.html").permitAll() // 메인 페이지
                        // 스웨거는 위쪽 WebSecurityCustomizer에서 이미 처리했으므로 여기선 생략해도 됨
                        .anyRequest().authenticated()
                );

        httpSecurity.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 요청을 허용할 프론트엔드 도메인 설정
        configuration.setAllowedOrigins(Arrays.asList(
                "https://www.jeocenter.shop",
                "https://jeocenter.shop"
        ));

        // 2. 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));

        // 4. 내보낼 헤더 설정 (필요시)
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // 5. 쿠키 및 인증 헤더 허용 (axios.withCredentials = true 대응)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}