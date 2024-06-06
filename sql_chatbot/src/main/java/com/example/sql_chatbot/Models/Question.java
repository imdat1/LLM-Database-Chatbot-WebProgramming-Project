package com.example.sql_chatbot.Models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;



    @Column(length = 1000)
    private String questionAnswer;

    // Define relationships
    // For example, if a question is related to a specific database:
    @ManyToOne()
    public Database database;


    // Getters and setters

    public Question(String questionText, Database database) {
        this.questionText = questionText;
        this.database = database;
    }

    public Question(String questionText, String questionAnswer, Database database) {
        this.questionText = questionText;
        this.questionAnswer = questionAnswer;
        this.database = database;
    }

    public Question() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

}

