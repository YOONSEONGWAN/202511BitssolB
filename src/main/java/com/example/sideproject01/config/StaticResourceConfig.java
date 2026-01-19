package com.example.sideproject01.config;

import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("upload").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}
