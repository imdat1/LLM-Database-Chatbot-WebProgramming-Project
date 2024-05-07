package com.example.sql_chatbot.Repository;

import com.example.sql_chatbot.Models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByDatabaseId(Long id);
}

