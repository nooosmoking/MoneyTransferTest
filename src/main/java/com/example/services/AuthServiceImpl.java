package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.User;
import com.example.repositories.UsersRepository;
import com.example.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService{
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void signUp(SignupRequest signupRequest) throws UserAlreadyExistsException {
        String login = signupRequest.getLogin();
        if (usersRepository.findByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException("User with login \"" +login+"\" already exists.");
        }
        String token = jwtTokenProvider.createToken(login);
        usersRepository.save(new User(login, passwordEncoder.encode(signupRequest.getPassword()), 0, token));
    }

    @Override
    public void signIn(SigninRequest signinRequest) throws NoSuchUserException{
        String login = signinRequest.getLogin();
        Optional<User> optionalUser = usersRepository.findByLogin(login);
        if(optionalUser.isEmpty()) {
            throw new NoSuchUserException("No such user with login \"" + login + "\".");
        }
         optionalUser.filter(user -> passwordEncoder.matches(signinRequest
                 .getPassword(), user.getPassword())).isPresent();
    }
}
