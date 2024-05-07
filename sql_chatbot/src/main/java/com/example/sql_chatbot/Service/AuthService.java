package com.example.sql_chatbot.Service;

import com.example.sql_chatbot.Models.User;

import java.util.List;

public interface AuthService {
    User login(String username, String password);

    List<User> findAll();

}
