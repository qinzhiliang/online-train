package com.intyt.online_train.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // ...
    private UsernameAuthenticationConfig usernameAuthenticationConfig;
    private AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private ExitSuccessHandler exitSuccessHandler;

    @Autowired
    public void setExitSuccessHandler(ExitSuccessHandler exitSuccessHandler) {
        this.exitSuccessHandler = exitSuccessHandler;
    }

    @Autowired
    public void setUsernameAuthenticationConfig(UsernameAuthenticationConfig usernameAuthenticationConfig) {
        this.usernameAuthenticationConfig = usernameAuthenticationConfig;
    }

    @Autowired
    public void setAuthenticationAccessDeniedHandler(AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler) {
        this.authenticationAccessDeniedHandler = authenticationAccessDeniedHandler;
    }

    @Autowired
    public void setAuthenticationEntryPointHandler(AuthenticationEntryPointHandler authenticationEntryPointHandler) {
        this.authenticationEntryPointHandler = authenticationEntryPointHandler;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/api/**")
//                .antMatchers("/hasa/**")
                .authenticated()
                .and()
                .apply(usernameAuthenticationConfig)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointHandler)//无法判断权限(未登录)
                .accessDeniedHandler(authenticationAccessDeniedHandler)//权限不足
                .and()
                .rememberMe()
                .tokenValiditySeconds(60 * 60 * 24 * 3)
                .and()
                .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(exitSuccessHandler);
    }
}
