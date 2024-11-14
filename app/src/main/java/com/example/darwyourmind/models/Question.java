package com.example.darwyourmind.models;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private HashMap<String, String> explanations;

    public Question(String questionText, ArrayList<String> options, JSONObject explanationObj) {
        this.questionText = questionText;
        this.options = options;
        this.explanations = new HashMap<>();
        for (String option : options) {
            try {
                this.explanations.put(option, explanationObj.getString(option));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getExplanation(String answer) {
        return explanations.getOrDefault(answer, "No explanation available.");
    }
}
