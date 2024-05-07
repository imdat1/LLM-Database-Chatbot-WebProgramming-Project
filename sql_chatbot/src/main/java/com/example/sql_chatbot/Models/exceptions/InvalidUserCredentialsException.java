package com.example.sql_chatbot.Models.exceptions;

public class InvalidUserCredentialsException extends RuntimeException{
    public InvalidUserCredentialsException() {
        super("Invalid Arguments Exception");
    }
}
