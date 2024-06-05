package com.example.security.jwt;

import com.example.models.User;

public class JwtUserFactory {
    public JwtUserFactory() {
    }

    public static JwtUser create(User user){
        return new JwtUser(user.getId(), user.getLogin(), user.getPassword(), user.getBalance(), true);
    }


}
