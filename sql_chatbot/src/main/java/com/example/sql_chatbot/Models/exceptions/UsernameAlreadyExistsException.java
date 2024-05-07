package com.example.sql_chatbot.Models.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super(String.format("User with %s already exists", username));
    }
}
