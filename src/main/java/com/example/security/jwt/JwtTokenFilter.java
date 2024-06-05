package com.example.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Map;

public class JwtTokenFilter {
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void doFilter(Map<String, String> headers, FilterChain filterChain){
        String token = jwtTokenProvider.resolveToken(headers);
        if(token!=null && jwtTokenProvider.validateToken(token)){
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication!=null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }
}

