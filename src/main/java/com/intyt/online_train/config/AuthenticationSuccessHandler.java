package com.intyt.online_train.config;

import com.intyt.online_train.domain.User;
import com.intyt.online_train.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 登陆成功
@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private UserServiceImpl userService;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = userService.current();
        response.setContentType("text/html;charset=UTF-8");
//        response.addHeader( "Access-Control-Allow-Origin", "http://10.18.43.3:8000"); // 允许跨域
        response.addHeader( "Access-Control-Allow-Origin","http://127.0.0.1:8000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        PrintWriter out = response.getWriter();
        out.flush();
        String s = String.valueOf(user.getRole().getId());
        out.write(s, 0, s.length());
        out.flush();
        out.close();
    }
}
