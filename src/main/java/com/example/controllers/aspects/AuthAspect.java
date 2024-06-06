package com.example.controllers.aspects;

import com.example.exceptions.JwtAuthenticationException;
import com.example.security.jwt.JwtTokenProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class AuthAspect {
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthAspect(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Before("@annotation(AuthRequired)")
    public void auth(ProceedingJoinPoint joinPoint) throws Throwable {
        Optional<Object> object = Arrays.stream(joinPoint
                        .getArgs())
                .filter(o -> o instanceof Map)
                .findFirst();
        if (object.isPresent()) {
            Map<String, String> headers = (Map<String, String>) object.get();
                jwtTokenProvider.doFilter(headers);
        }

        joinPoint.proceed();

    }
}
