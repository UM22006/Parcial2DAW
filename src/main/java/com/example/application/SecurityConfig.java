package com.example.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/VAADIN/**",
                    "/frontend/**",
                    "/images/**",
                    "/icons/**",
                    "/manifest.webmanifest",
                    "/sw.js",
                    "/offline.html",
                    "/h2-console/**",
                    "/favicon.ico",
                    "/static/**",
                    "/js/**",
                    "/css/**",
                    "/webjars/**",
                    "/login",                 // Ruta de login
                    "/logout",                // Ruta de logout
                    "/v-r/**",                // 💥 Rutas internas Vaadin
                    "/v/**",                  // 💥 Para evitar los errores 403 UIDL
                    "/UIDL/**",               // 💥 UIDL es clave para reconexión
                    "/HEARTBEAT/**"           // 💥 Heartbeat Vaadin
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/inicio", true)
            )
            .logout(logout -> logout.logoutSuccessUrl("/"))
            .csrf(csrf -> csrf.disable()); // ⚠️ Temporalmente para pruebas, mejor usar token CSRF en producción
        return http.build();
    }
}
