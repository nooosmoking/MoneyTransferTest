package com.example.controllers.aspects;

import com.example.exceptions.JwtAuthenticationException;
import com.example.models.Request;
import com.example.models.User;
import com.example.repositories.UsersRepository;
import com.example.security.jwt.JwtTokenProvider;
import org.aspectj.lang.JoinPoint;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersRepository usersRepository;

    @Autowired
    public AuthAspect(JwtTokenProvider jwtTokenProvider, UsersRepository usersRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.usersRepository = usersRepository;
    }

    @Before("@annotation(AuthRequired)")
    public void auth(JoinPoint joinPoint) throws Throwable {
        Optional<Object> object = Arrays.stream(joinPoint
                        .getArgs())
                .filter(o -> o instanceof Request)
                .findFirst();
        if (object.isPresent()) {
            Request request = (Request) object.get();
            Map<String, String> headers = request.getHeaders();
                String login = jwtTokenProvider.doFilter(headers);
                request.setLogin(login);
        }
    }
}
