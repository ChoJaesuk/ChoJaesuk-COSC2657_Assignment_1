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

    private TextView questionTextView, explanationTextView;
    private ImageView drawingImageView;
    private RadioGroup optionsGroup;
    private Button submitButton, nextButton;

    private ArrayList<Question> questionsList;
    private int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionTextView = findViewById(R.id.questionTitle);
        explanationTextView = findViewById(R.id.explanationTextView);
        drawingImageView = findViewById(R.id.drawingImageView);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);

        // Load the drawing image
        String drawingPath = getIntent().getStringExtra("drawingPath");
        if (drawingPath != null) {
            Bitmap bitmap = loadImageFromPath(drawingPath);
            drawingImageView.setImageBitmap(bitmap);
        }

        loadQuestionsFromJson("Psychology Test");
        displayQuestion(currentQuestionIndex);

        // Initially disable the submit button
        submitButton.setEnabled(false);

        // Enable submit button only when an option is selected
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            submitButton.setEnabled(checkedId != -1); // Enable if an option is selected
        });

        submitButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedOption = findViewById(selectedId);
                String answer = selectedOption.getText().toString();
                showExplanation(answer);
            } else {
                // Show a message if no option is selected
                Toast.makeText(QuestionActivity.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questionsList.size()) {
                displayQuestion(currentQuestionIndex);
            } else {
                // Go back to HomeActivity after all questions
                Intent homeIntent = new Intent(QuestionActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
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

        explanationTextView.setText("");
        explanationTextView.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false); // Disable until an option is selected
        nextButton.setVisibility(View.GONE);
    }

    private void showExplanation(String answer) {
        Question currentQuestion = questionsList.get(currentQuestionIndex);
        String explanation = currentQuestion.getExplanation(answer);
        explanationTextView.setText(explanation);
        explanationTextView.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
    }
}
