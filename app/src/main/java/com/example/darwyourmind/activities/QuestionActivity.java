package com.example.darwyourmind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class QuestionActivity extends BaseActivity {

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

        // Set title for the ActionBar
        setTitle("Questions");

        // Enable ActionBar's back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        questionTextView = findViewById(R.id.questionTitle);
        drawingImageView = findViewById(R.id.drawingImageView);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitButton = findViewById(R.id.submitButton);
        TextView questionTitleTextView = findViewById(R.id.questionTitleTextView);

        // Intent에서 테스트 타이틀 가져오기
        Intent intent = getIntent();
        String testTitle = intent.getStringExtra("testTitle");

        // 테스트 타이틀 표시
        if (testTitle != null) {
            questionTitleTextView.setText(testTitle);
        }

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

        // Pass the test title to ResultActivity
        String testTitle = getIntent().getStringExtra("testTitle");
        if (testTitle != null) {
            resultIntent.putExtra("testTitle", testTitle); // Include the test title
        }

        startActivity(resultIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); // Replace with your menu file name
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Call the back button behavior
            return true;
        } else if (item.getItemId() == R.id.action_home) {
            // 홈 버튼 클릭 시 홈으로 이동
            navigateToHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class); // HomeMenuActivity로 변경 필요
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Questions?");
        builder.setMessage("Do you want to exit the question screen? Unsaved progress will be lost.");

        builder.setPositiveButton("Yes", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
