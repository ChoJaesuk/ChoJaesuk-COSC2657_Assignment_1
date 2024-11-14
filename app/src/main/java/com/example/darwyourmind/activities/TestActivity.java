package com.example.darwyourmind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.darwyourmind.R;
import com.example.darwyourmind.views.DrawingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    private TextView testTitle;
    private TextView drawingPromptTextView;
    private DrawingView drawingView;
    private Button clearButton, submitButton, redButton, blueButton, greenButton, eraserButton;
    private ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testTitle = findViewById(R.id.testTitle);
        drawingPromptTextView = findViewById(R.id.drawingPromptTextView);
        drawingView = findViewById(R.id.drawingView);
        clearButton = findViewById(R.id.clearButton);
        submitButton = findViewById(R.id.submitButton);
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        greenButton = findViewById(R.id.greenButton);
        eraserButton = findViewById(R.id.eraserButton);

        // Get category information from intent
        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        // Load questions from JSON based on the category
        questions = new ArrayList<>();
        String drawingPrompt = loadQuestionsFromJson(category);

        // Set the category name and drawing prompt
        testTitle.setText(category);
        drawingPromptTextView.setText(drawingPrompt);

        // Clear button functionality
        clearButton.setOnClickListener(view -> drawingView.clearDrawing());

        // Color change buttons
        redButton.setOnClickListener(v -> drawingView.setColor(Color.RED));
        blueButton.setOnClickListener(v -> drawingView.setColor(Color.BLUE));
        greenButton.setOnClickListener(v -> drawingView.setColor(Color.GREEN));

        // Eraser button
        eraserButton.setOnClickListener(v -> drawingView.enableEraser());

        // Submit button to move to QuestionActivity
        submitButton.setOnClickListener(view -> {
            Log.d("TestActivity", "Submit button clicked");
            String drawingPath = saveDrawingToFile();  // Save drawing and get the file path
            Intent questionIntent = new Intent(TestActivity.this, QuestionActivity.class);
            questionIntent.putStringArrayListExtra("questions", questions);
            questionIntent.putExtra("drawingPath", drawingPath);  // Pass the drawing file path
            startActivity(questionIntent);
        });
    }

    // Method to save the drawing to a file
    private String saveDrawingToFile() {
        File drawingFile = new File(getCacheDir(), "drawing.png");
        try (FileOutputStream out = new FileOutputStream(drawingFile)) {
            Bitmap bitmap = drawingView.getBitmap();  // Assuming DrawingView has a method to get the bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return drawingFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("TestActivity", "Error saving drawing", e);
            return null;
        }
    }

    private String loadQuestionsFromJson(String categoryName) {
        String drawingPrompt = "";
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

            // Find the matching category
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                if (category.getString("name").equals(categoryName)) {
                    drawingPrompt = category.getString("drawing_prompt");
                    JSONArray questionsArray = category.getJSONArray("questions");

                    // Load questions into the ArrayList
                    for (int j = 0; j < questionsArray.length(); j++) {
                        questions.add(questionsArray.getString(j));
                    }
                    break;
                }
            }

            reader.close();
        } catch (Exception e) {
            Log.e("TestActivity", "Error reading JSON file", e);
        }

        return drawingPrompt;
    }
}
