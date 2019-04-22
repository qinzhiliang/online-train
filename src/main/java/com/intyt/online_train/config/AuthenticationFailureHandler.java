package com.intyt.online_train.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(400);
//        response.addHeader( "Access-Control-Allow-Origin", "http://10.18.43.3:8000"); // 允许跨域
        response.addHeader( "Access-Control-Allow-Origin","http://127.0.0.1:8000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        PrintWriter out = response.getWriter();
        String s = "用户名或密码错误";
        out.write(s, 0, s.length());
        out.flush();
        out.close();
    }
}
