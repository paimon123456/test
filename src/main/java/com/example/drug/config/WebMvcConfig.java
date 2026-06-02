package com.example.drug.config;

import com.example.drug.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/admin/login",       // 登录接口
                    "/admin/register",    // 注册接口
                    "/**/*.html",         // 静态HTML页面
                    "/**/*.css",          // 静态CSS
                    "/**/*.js",           // 静态JS
                    "/**/*.png",          // 静态图片
                    "/**/*.jpg",
                    "/**/*.ico",
                    "/**/*.svg",
                    "/**/*.woff",
                    "/**/*.woff2",
                    "/**/*.ttf",
                    "/media/**",          // 媒体资源
                    "/error"              // 错误页面
                );
    }
}
