package com.example.sql_chatbot.Service.Impl;

import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.exceptions.InvalidArgumentsException;
import com.example.sql_chatbot.Models.exceptions.InvalidUserCredentialsException;
import com.example.sql_chatbot.Repository.UserRepository;
import com.example.sql_chatbot.Service.AuthService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User login(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        return userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(InvalidUserCredentialsException::new);

    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }
}
