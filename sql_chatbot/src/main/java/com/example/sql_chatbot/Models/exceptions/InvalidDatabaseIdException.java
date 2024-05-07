package com.example.sql_chatbot.Models.exceptions;

public class InvalidDatabaseIdException extends RuntimeException{
    public InvalidDatabaseIdException() {
        super("Invalid Database Id Exception!");
    }
}
