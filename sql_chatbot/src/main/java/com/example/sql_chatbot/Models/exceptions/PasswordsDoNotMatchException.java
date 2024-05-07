package com.example.sql_chatbot.Models.exceptions;

public class PasswordsDoNotMatchException extends RuntimeException {
    public PasswordsDoNotMatchException() {
        super("Passwords Do Not Match Exception!");
    }
}
