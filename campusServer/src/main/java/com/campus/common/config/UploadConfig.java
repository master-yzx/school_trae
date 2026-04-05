package com.campus.common.config;

import com.campus.common.utils.UploadPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将本地 upload 目录映射为 /upload/** 静态资源
        String location = UploadPaths.getUploadDir().toUri().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location);
    }
}

