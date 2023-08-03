package com.fwea.loopychat;

import com.fwea.loopychat.service.SseEmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class LoopychatServerApplication {

    private static final String[] SWAGGER_PATHS = {
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/webjars/swagger-ui/**"
    };

    public static void main(String[] args) {
        SpringApplication.run(LoopychatServerApplication.class, args);
    }

    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @Bean("securityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var chain = http
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(SWAGGER_PATHS).permitAll()
                        //.requestMatchers("/api/channel/send").permitAll()
                        .requestMatchers("/api/channel/sse").permitAll()
                        .requestMatchers("/api/csrf").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/signup").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .rememberMe(customizer -> customizer.alwaysRemember(true).key("loopychat"))
                //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .build();

        var rememberMeServices = http.getSharedObject(RememberMeServices.class);
        beanFactory.registerSingleton("rememberMeServices", rememberMeServices);

        return chain;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    @Bean
    public SseEmitterService sseEmitter() {
        return new SseEmitterService();
    }*/
}
