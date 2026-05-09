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
                .addPathPatterns("/drug/**", "/admin/**")
                .excludePathPatterns("/admin/login", "/admin/register", "/admin/list", "/admin/status/**", "/drug/add", "/drug/update", "/drug/list", "/drug/status/**", "/drug/delete/**", "/login.html", "/drug.html", "/role.html", "/**/*.html");
    }
}
