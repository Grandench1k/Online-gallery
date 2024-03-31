package com.online.gallery;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(
        exclude = {RedisRepositoriesAutoConfiguration.class})
@EnableCaching()
@OpenAPIDefinition(info = @Info(title = "Online-gallery API", version = "1.0.0", description = "Service for work with online-gallery API"))
@SecurityScheme(name = "Authorization", scheme = "bearer", bearerFormat = "JWT", type = SecuritySchemeType.HTTP)
public class OnlineGalleryApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineGalleryApplication.class, args);
    }
}
