package com.example.sql_chatbot.Service.Impl;

import com.example.sql_chatbot.Models.Database;
import com.example.sql_chatbot.Models.Question;
import com.example.sql_chatbot.Models.User;
import com.example.sql_chatbot.Models.exceptions.InvalidDatabaseIdException;
import com.example.sql_chatbot.Models.exceptions.InvalidUsernameOrPasswordException;
import com.example.sql_chatbot.Repository.DatabaseRepository;
import com.example.sql_chatbot.Repository.QuestionRepository;
import com.example.sql_chatbot.Repository.UserRepository;
import com.example.sql_chatbot.Service.DatabaseService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final UserRepository userRepository;
    private final DatabaseRepository databaseRepository;

    private final QuestionRepository questionRepository;

    public DatabaseServiceImpl(UserRepository userRepository, DatabaseRepository databaseRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.databaseRepository = databaseRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Database getDatabaseById(Long id) {
        return this.databaseRepository.findById(id).orElseThrow(InvalidDatabaseIdException::new);
    }

    @Override
    public List<Database> findAll() {
        return this.databaseRepository.findAll();
    }

    @Override
    public List<Database> findAllForUser(String username) {
        return this.databaseRepository.findAllByUserUsername(username);
    }

    @Override
    public Database createDatabase(String name, String username, String password, String host, String databaseName, String userUsername) {
        User user = this.userRepository.findByUsername(userUsername).orElseThrow(InvalidUsernameOrPasswordException::new);
        return this.databaseRepository.save(new Database(name, username, password, host, databaseName, user));
    }

    @Override
    public Database deleteDatabase(Long id) {
        List<Question> questions = this.questionRepository.findAllByDatabaseId(id);
        for(int i=0; i<questions.size(); i++){
            Question question = questions.get(i);
            this.questionRepository.delete(question);
        }
        Database database = this.databaseRepository.findById(id).orElseThrow(InvalidDatabaseIdException::new);
        this.databaseRepository.delete(database);
        return database;
    }

    @Override
    public Database update(Long id, String name, String username, String password, String host, String databaseName, String userUsername) {
        User user = this.userRepository.findByUsername(userUsername).orElseThrow(InvalidUsernameOrPasswordException::new);
        Database database = this.databaseRepository.findById(id).orElseThrow(InvalidDatabaseIdException :: new);

        database.setName(name);
        database.setUsername(username);
        database.setPassword(password);
        database.setHost(host);
        database.setDatabaseName(databaseName);
        database.setUser(user);

        return this.databaseRepository.save(database);
    }

    @Override
    public Database getDatabaseByNameUsernamePasswordHostDBNameUserUsername(String name, String username, String password, String host, String databaseName, String userUsername) {
        return this.databaseRepository.findByNameAndUsernameAndPasswordAndHostAndDatabaseNameAndUserUsername(name,username,password,host,databaseName,userUsername);
    }
}
