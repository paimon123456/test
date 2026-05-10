package com.example.drug.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * HTML 页面缓存控制过滤器
 * 强制禁用 HTML 页面缓存，确保每次访问都获取最新版本
 */
@Component
public class HtmlNoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();
        
        // 只对 HTML 页面添加禁止缓存头
        if (requestURI.endsWith(".html")) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
        }
        
        chain.doFilter(request, response);
    }
}
