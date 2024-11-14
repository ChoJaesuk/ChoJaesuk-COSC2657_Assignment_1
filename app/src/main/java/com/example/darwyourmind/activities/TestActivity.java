package com.example.darwyourmind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.darwyourmind.R;
import com.example.darwyourmind.adapters.ColorSpinnerAdapter;
import com.example.darwyourmind.views.DrawingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TextView testTitle;
    private TextView drawingPromptTextView;
    private DrawingView drawingView;
    private Button clearButton, submitButton, eraserButton;
    private ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize view components
        testTitle = findViewById(R.id.testTitle);
        drawingPromptTextView = findViewById(R.id.drawingPromptTextView);
        drawingView = findViewById(R.id.drawingView);
        clearButton = findViewById(R.id.clearButton);
        submitButton = findViewById(R.id.submitButton);
        eraserButton = findViewById(R.id.eraserButton);

        Spinner colorSpinner = findViewById(R.id.colorSpinner);

        // Set color names and values
        List<String> colorNames = Arrays.asList("Black", "Red", "Blue", "Green");
        int[] colorValues = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN};

        // Set up color spinner adapter
        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(this, colorNames, colorValues);
        colorSpinner.setAdapter(adapter);

        // Color selection event listener for spinner
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawingView.setColor(colorValues[position]); // Set selected color
                drawingView.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default behavior
            }
        });

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

        // Eraser button functionality
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
