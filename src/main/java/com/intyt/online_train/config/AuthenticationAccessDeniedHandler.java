package com.intyt.online_train.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 权限不足
@Configuration
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
//        httpServletResponse.addHeader( "Access-Control-Allow-Origin", "http://10.18.43.3:8000"); // 允许跨域
        httpServletResponse.addHeader( "Access-Control-Allow-Origin","http://127.0.0.1:8000");
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setStatus(403);
    }
}
