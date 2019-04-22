package com.intyt.online_train.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 登出成功
@Component
public class ExitSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
//        response.addHeader( "Access-Control-Allow-Origin", "http://10.18.43.3:8000"); // 允许跨域
        response.addHeader( "Access-Control-Allow-Origin","http://127.0.0.1:8000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        PrintWriter out = response.getWriter();
        out.write("登出成功");
        out.flush();
        out.close();
    }
}
