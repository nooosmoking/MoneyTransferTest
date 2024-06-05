package com.example.services;

import com.example.exceptions.NoSuchUserException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.models.SigninRequest;
import com.example.models.SignupRequest;
import com.example.models.User;
import com.example.repositories.UsersRepository;
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

    @Autowired
    public AuthServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signUp(SigninRequest signinRequest) throws UserAlreadyExistsException {
        String login = signinRequest.getLogin();
        if (usersRepository.findByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException("User with login \"" +login+"\" already exists.");
        }
        usersRepository.save(new User(login, passwordEncoder.encode(signinRequest.getPassword()), 0));
    }

    @Override
    public void signIn(SignupRequest signupRequest) throws NoSuchUserException{
        String login = signupRequest.getLogin();
        Optional<User> optionalUser = usersRepository.findByLogin(login);
        if(optionalUser.isEmpty()) {
            throw new NoSuchUserException("No such user with login \"" + login + "\".");
        }
         optionalUser.filter(user -> passwordEncoder.matches(signupRequest.getPassword(), user.getPassword())).isPresent();
    }
}
