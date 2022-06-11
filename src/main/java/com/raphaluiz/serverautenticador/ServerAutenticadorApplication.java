package com.raphaluiz.serverautenticador;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Security;

@SpringBootApplication
public class ServerAutenticadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerAutenticadorApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/auth").allowedOrigins("http://localhost:3000");
                registry.addMapping("/create-user").allowedOrigins("http://localhost:3000");
                registry.addMapping("/two-factor-auth").allowedOrigins("http://localhost:3000");
            }
        };
    }

}
