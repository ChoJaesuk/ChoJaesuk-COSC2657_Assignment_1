package com.example.darwyourmind.models;

import java.util.List;

public class Category {
    private String name;
    private String drawingPrompt;
    private List<String> questions;
    private String description; // 추가된 필드

    // Constructor to initialize the fields
    public Category() {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters and Setters for drawingPrompt
    public String getDrawingPrompt() {
        return drawingPrompt;
    }

    public void setDrawingPrompt(String drawingPrompt) {
        this.drawingPrompt = drawingPrompt;
    }

    // Getters and Setters for questions
    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    // Getters and Setters for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
