package com.example.drug.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源缓存配置
 * 开发环境下禁用HTML页面缓存，确保修改后立即生效
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 方案1：在 application.yml 中已配置全局不缓存
        // spring.web.resources.cache.period=0
        // 这里不需要额外配置，避免与 yml 冲突
    }
}
