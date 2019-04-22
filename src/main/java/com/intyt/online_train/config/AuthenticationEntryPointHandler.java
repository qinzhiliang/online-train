package com.intyt.online_train.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//无法判断权限(未登录)
@Configuration
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.addHeader( "Access-Control-Allow-Origin", "http://10.18.43.3:8000"); // 允许跨域
        response.addHeader( "Access-Control-Allow-Origin","http://127.0.0.1:8000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(403);
    }

}
