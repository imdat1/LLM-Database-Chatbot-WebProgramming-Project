package com.example.sql_chatbot.Service.Impl;

import com.example.sql_chatbot.Models.Database;
import com.example.sql_chatbot.Models.Question;
import com.example.sql_chatbot.Models.exceptions.InvalidDatabaseIdException;
import com.example.sql_chatbot.Repository.DatabaseRepository;
import com.example.sql_chatbot.Repository.QuestionRepository;
import com.example.sql_chatbot.Service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final DatabaseRepository databaseRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, DatabaseRepository databaseRepository) {
        this.questionRepository = questionRepository;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public Question getQuestionById(Long id) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        return questionOptional.orElse(null);
    }

    @Override
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question createQuestion(String question, String answer, Long id) {
        Database database = this.databaseRepository.findById(id).orElseThrow(InvalidDatabaseIdException::new);
        return this.questionRepository.save(new Question(question,answer,database));
    }

    @Override
    public List<Question> findAllForDatabase(Long id) {
        return questionRepository.findAllByDatabaseId(id);
    }
}
