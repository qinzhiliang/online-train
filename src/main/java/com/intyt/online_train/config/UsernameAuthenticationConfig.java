package com.intyt.online_train.config;

import com.intyt.online_train.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class UsernameAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private UserServiceImpl userService;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Autowired
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        // 实例化过滤器
        UsernameAuthenticationFilter usernameAuthenticationFilter = new UsernameAuthenticationFilter();
        // 设置一个Manager
        usernameAuthenticationFilter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        // 自定义handler
        usernameAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        usernameAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        // 实例化Provider
        UsernameAuthenticationProvider usernameAuthenticationProvider = new UsernameAuthenticationProvider(userService);
        // 把usernameAuthenticationFilter 放到UsernamePasswordAuthenticationFilter 前面
        builder.authenticationProvider(usernameAuthenticationProvider)
                .addFilterBefore(usernameAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }


}
