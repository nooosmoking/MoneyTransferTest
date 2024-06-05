package com.example.security;

import com.example.models.User;
import com.example.repositories.UsersRepository;
import com.example.security.jwt.JwtUserFactory;
import com.example.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Autowired
    public JwtUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user= usersRepository.findByLogin(s);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User with login \" "+s+"\" is not exists.");
        }
        return JwtUserFactory.create(user.get());
    }
}
