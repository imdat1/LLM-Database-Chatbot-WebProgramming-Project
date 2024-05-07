package com.example.sql_chatbot.Repository;

import com.example.sql_chatbot.Models.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    // You can add custom query methods here if needed
//String username, String password, String host, String databaseName, String userUsername
    List<Database> findAllByUserUsername(String username);
    Database findByNameAndUsernameAndPasswordAndHostAndDatabaseNameAndUserUsername(String name, String username, String password, String host, String databaseName, String userUsername);


}