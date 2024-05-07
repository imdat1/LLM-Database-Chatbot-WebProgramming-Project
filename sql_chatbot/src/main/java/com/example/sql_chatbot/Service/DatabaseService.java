package com.example.sql_chatbot.Service;

import com.example.sql_chatbot.Models.Database;
import com.example.sql_chatbot.Models.Question;
import com.example.sql_chatbot.Models.User;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Set;

public interface DatabaseService {
    Database getDatabaseById(Long id);

    List<Database> findAll();

    List<Database> findAllForUser(String username);

    Database createDatabase(String name, String username, String password, String host, String databaseName, String userUsername);

    Database deleteDatabase(Long id);

    Database update(Long id, String name, String username, String password, String host, String databaseName, String userUsername);
    Database getDatabaseByNameUsernamePasswordHostDBNameUserUsername (String name, String username, String password, String host, String databaseName, String userUsername);
}
