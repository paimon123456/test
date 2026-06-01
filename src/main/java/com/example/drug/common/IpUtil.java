package com.example.drug.common;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP地址获取工具类
 * 支持反向代理场景（Nginx/Apache），优先从代理头中获取真实IP
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取客户端真实IP地址
     * 优先级：X-Forwarded-For > X-Real-IP > Proxy-Client-IP > WL-Proxy-Client-IP > RemoteAddr
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;

        // 1. X-Forwarded-For (Nginx/HAProxy/阿里云SLB)
        ip = request.getHeader("X-Forwarded-For");
        if (isValid(ip)) {
            // X-Forwarded-For 格式：client, proxy1, proxy2，取第一个即真实客户端IP
            int index = ip.indexOf(',');
            if (index > 0) {
                ip = ip.substring(0, index);
            }
            return ip.trim();
        }

        // 2. X-Real-IP (Nginx)
        ip = request.getHeader("X-Real-IP");
        if (isValid(ip)) {
            return ip.trim();
        }

        // 3. Proxy-Client-IP (Apache)
        ip = request.getHeader("Proxy-Client-IP");
        if (isValid(ip)) {
            return ip.trim();
        }

        // 4. WL-Proxy-Client-IP (WebLogic)
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValid(ip)) {
            return ip.trim();
        }

        // 5. 兜底：直接连接IP
        ip = request.getRemoteAddr();
        if (ip == null) {
            return "";
        }

        // 处理 IPv6 本地回环地址 0:0:0:0:0:0:0:1 -> 127.0.0.1
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }

    private static boolean isValid(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip.trim());
    }
}
