package com.example.sql_chatbot.Service;

import com.example.sql_chatbot.Models.Database;
import com.example.sql_chatbot.Models.Question;

import java.util.List;

public interface QuestionService {
    Question getQuestionById(Long id);
    Question saveQuestion(Question question);
    List<Question> getAllQuestions();

    Question createQuestion(String question, String answer, Long id);

    List<Question> findAllForDatabase(Long id);
}
