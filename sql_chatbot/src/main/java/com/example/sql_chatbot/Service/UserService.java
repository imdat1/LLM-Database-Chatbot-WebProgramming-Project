package com.example.sql_chatbot.Service;

import com.example.sql_chatbot.Models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.sql_chatbot.Models.enumerations.Role;

import java.util.List;


public interface UserService extends UserDetailsService {
    User register(String username, String password, String repeatPassword, String huggingFaceAPIToken, Role role);
    List<User> findAll();
    User findByUsername(String username);
    User deleteUser(User user);
    User updateUsername(String oldUsername, String newUsername);

    User updateUserCredentials(String username, String huggingFaceAPIToken, Role role);
    User updateUserPassword(String username, String password, String repeatedPassword);
    User updateUser(User user);
}
