package com.example.sql_chatbot.Config;

import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.enumerations.Role;
import com.example.sql_chatbot.Repository.UserRepository;
import com.example.sql_chatbot.Service.DatabaseService;
import com.example.sql_chatbot.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;
    @Autowired
    DatabaseService databaseService;

    @Override
    public void run(String...args) throws Exception {

        User admin = new User("deannaste123",passwordEncoder.encode("1234"),System.getenv("HUGGING_FACE_TOKEN"), com.example.sql_chatbot.Models.enumerations.Role.ROLE_ADMIN);
        try {
            if(!(admin.getUsername() != this.userService.findByUsername("deannaste123").getUsername())){
                userRepository.save(admin);
                this.databaseService.createDatabase("For testing", "northwind", "northwind","northwind:5432", "northwind", admin.getUsername());
            }
        }
        catch (UsernameNotFoundException exception){
            userRepository.save(admin);
            this.databaseService.createDatabase("For testing", "northwind", "northwind","northwind:5432", "northwind", admin.getUsername());
        }
    }
}