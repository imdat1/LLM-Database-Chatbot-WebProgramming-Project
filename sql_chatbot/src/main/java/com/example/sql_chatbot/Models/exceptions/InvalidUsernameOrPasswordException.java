package com.example.sql_chatbot.Models.exceptions;

public class InvalidUsernameOrPasswordException extends RuntimeException {
    public InvalidUsernameOrPasswordException() {
        super("Invalid Username or Password Exception!");
    }
}
