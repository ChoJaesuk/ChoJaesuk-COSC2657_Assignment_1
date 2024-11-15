package com.example.darwyourmind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.darwyourmind.R;
import com.example.darwyourmind.models.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    private TextView questionTextView;
    private ImageView drawingImageView;
    private RadioGroup optionsGroup;
    private Button submitButton;

    private ArrayList<Question> questionsList;
    private ArrayList<String[]> resultList; // Store question, answer, and explanation
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionTextView = findViewById(R.id.questionTitle);
        drawingImageView = findViewById(R.id.drawingImageView);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitButton = findViewById(R.id.submitButton);

        resultList = new ArrayList<>(); // Initialize result list

        // Load the drawing image
        String drawingPath = getIntent().getStringExtra("drawingPath");
        if (drawingPath != null) {
            Bitmap bitmap = loadImageFromPath(drawingPath);
            drawingImageView.setImageBitmap(bitmap);
        }

        String selectedCategory = getIntent().getStringExtra("category");
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

// 선택된 카테고리를 기반으로 질문 로드
        loadQuestionsFromJson(selectedCategory);


        // Load questions based on the selected category
        loadQuestionsFromJson(selectedCategory);
        displayQuestion(currentQuestionIndex);

        // Initially disable the submit button
        submitButton.setEnabled(false);

        // Enable submit button only when an option is selected
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            submitButton.setEnabled(checkedId != -1);
        });

        submitButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedOption = findViewById(selectedId);
                String answer = selectedOption.getText().toString();

                // Save the question, selected answer, and explanation
                Question currentQuestion = questionsList.get(currentQuestionIndex);
                String explanation = currentQuestion.getExplanation(answer);
                resultList.add(new String[]{currentQuestion.getQuestionText(), answer, explanation});

                currentQuestionIndex++;
                if (currentQuestionIndex < questionsList.size()) {
                    displayQuestion(currentQuestionIndex); // Display the next question
                } else {
                    // Navigate to ResultActivity after the last question
                    navigateToResultActivity();
                }
            } else {
                Toast.makeText(QuestionActivity.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap loadImageFromPath(String path) {
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("QuestionActivity", "Error loading image", e);
            return null;
        }
    }

    private void loadQuestionsFromJson(String categoryName) {
        questionsList = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.questions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonString.toString());
            JSONArray categoriesArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                if (category.getString("name").equals(categoryName)) {
                    JSONArray questionsArray = category.getJSONArray("questions");

                    for (int j = 0; j < questionsArray.length(); j++) {
                        JSONObject questionObj = questionsArray.getJSONObject(j);
                        String questionText = questionObj.getString("question");
                        JSONArray optionsArray = questionObj.getJSONArray("options");

                        ArrayList<String> options = new ArrayList<>();
                        for (int k = 0; k < optionsArray.length(); k++) {
                            options.add(optionsArray.getString(k));
                        }

                        JSONObject explanationObj = questionObj.getJSONObject("explanation");
                        questionsList.add(new Question(questionText, options, explanationObj));
                    }
                    break;
                }
            }

            reader.close();
        } catch (Exception e) {
            Log.e("QuestionActivity", "Error reading JSON file", e);
        }
    }

    private void displayQuestion(int index) {
        Question currentQuestion = questionsList.get(index);
        questionTextView.setText(currentQuestion.getQuestionText());
        optionsGroup.removeAllViews();

        for (String option : currentQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            optionsGroup.addView(radioButton);
        }

        submitButton.setEnabled(false); // Disable until an option is selected
    }

    private void navigateToResultActivity() {
        Intent resultIntent = new Intent(QuestionActivity.this, ResultActivity.class);
        resultIntent.putExtra("resultList", resultList); // Pass result list to ResultActivity

        // Pass the drawing path to ResultActivity
        String drawingPath = getIntent().getStringExtra("drawingPath");
        if (drawingPath != null) {
            resultIntent.putExtra("drawingPath", drawingPath); // Include the drawing path
        }

        startActivity(resultIntent);
        finish();
    }
}
