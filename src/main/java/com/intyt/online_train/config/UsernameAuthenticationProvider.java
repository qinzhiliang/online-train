package com.intyt.online_train.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;

public class UsernameAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;

    public UsernameAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 把map中的东西取出来
        HashMap<String, String> principal = (HashMap<String, String>) authentication.getPrincipal();
        String username = principal.get("username");
        String password = principal.get("password");
        // 认证
        if (username.isEmpty() || password.isEmpty()) {
            throw new InternalAuthenticationServiceException("Authentication failure");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (userDetails == null || !encoder.matches(password, userDetails.getPassword())) {
            throw new UsernameNotFoundException("username or password is not correct");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
