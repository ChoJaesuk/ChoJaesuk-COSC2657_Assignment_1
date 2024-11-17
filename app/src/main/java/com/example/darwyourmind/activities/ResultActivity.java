package com.example.darwyourmind.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.darwyourmind.R;
import com.example.darwyourmind.adapters.TestHistoryManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ResultActivity extends BaseActivity {

    private ImageView drawingImageView;
    private LinearLayout resultContainer;
    private Button saveButton; // Save Button for saving results
    private Button shareButton; // Share Button for sharing results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        drawingImageView = findViewById(R.id.drawingImageView);
        resultContainer = findViewById(R.id.resultContainer);
        saveButton = findViewById(R.id.saveButton); // Initialize Save Button
        shareButton = findViewById(R.id.shareButton); // Initialize Share Button

        // Set title for the ActionBar
        setTitle("Result");

        // Load the test title
        String testTitle = getIntent().getStringExtra("testTitle");
        if (testTitle != null) {
            Log.d("ResultActivity", "Test Title: " + testTitle);
            TextView testTitleView = findViewById(R.id.testTitleTextView); // Ensure this ID exists in the layout
            testTitleView.setText(testTitle);
        }

        // Load the drawing image
        String drawingPath = getIntent().getStringExtra("drawingPath");
        Bitmap bitmap = loadImageFromPath(drawingPath);
        if (bitmap != null) {
            drawingImageView.setImageBitmap(bitmap);
        } else {
            Log.e("ResultActivity", "Failed to load drawing image.");
        }

        // Get the result list from the intent
        ArrayList<String[]> resultList = (ArrayList<String[]>) getIntent().getSerializableExtra("resultList");
        if (resultList == null || resultList.isEmpty()) {
            Toast.makeText(this, "No test results found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add each question, answer, and explanation as a card
        for (String[] result : resultList) {
            addResultCard(result[0], result[1], result[2]);
        }

        // Save Button functionality
        saveButton.setOnClickListener(v -> {
            if (testTitle == null || drawingPath == null) {
                Toast.makeText(this, "Missing test title or drawing path!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure a unique file name for each drawing in history
            String uniqueDrawingPath = saveUniqueDrawing(bitmap);

            // Save the test result to history
            TestHistoryManager historyManager = new TestHistoryManager(this);
            historyManager.saveTestResult(testTitle, uniqueDrawingPath, resultList);
            Toast.makeText(this, "Test result saved successfully!", Toast.LENGTH_SHORT).show();
        });

        // Share Button functionality
        shareButton.setOnClickListener(v -> {
            try {
                File sharedImage = createShareableImage();
                if (sharedImage != null) {
                    shareResult(sharedImage);
                } else {
                    Toast.makeText(this, "Error preparing result for sharing!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("ResultActivity", "Error sharing result", e);
                Toast.makeText(this, "Error occurred while sharing!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds a result card to the result container.
     */
    private void addResultCard(String question, String answer, String explanation) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.border_imageview_background);
        card.setPadding(16, 16, 16, 16);

        // Set layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16); // Margin between cards
        card.setLayoutParams(params);

        // Question Text
        TextView questionTextView = new TextView(this);
        questionTextView.setText("Question: " + question);
        questionTextView.setTextSize(16);
        questionTextView.setTextColor(getResources().getColor(R.color.primary_text));
        questionTextView.setPadding(0, 0, 0, 8);

        // Answer Text
        TextView answerTextView = new TextView(this);
        answerTextView.setText("Your Answer: " + answer);
        answerTextView.setTextSize(16);
        answerTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        answerTextView.setPadding(0, 0, 0, 8);

        // Explanation Text
        TextView explanationTextView = new TextView(this);
        explanationTextView.setText("Explanation: " + explanation);
        explanationTextView.setTextSize(14);
        explanationTextView.setTextColor(Color.DKGRAY);

        // Add all elements to the card
        card.addView(questionTextView);
        card.addView(answerTextView);
        card.addView(explanationTextView);

        // Add the card to the container
        resultContainer.addView(card);
    }

    /**
     * Loads a bitmap image from the given file path.
     */
    private Bitmap loadImageFromPath(String path) {
        try {
            File file = new File(path);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the bitmap image to a unique file and returns the file path.
     */
    private String saveUniqueDrawing(Bitmap bitmap) {
        try {
            String uniqueFileName = "drawing_" + System.currentTimeMillis() + ".png"; // Unique file name
            File drawingFile = new File(getCacheDir(), uniqueFileName);

            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new java.io.FileOutputStream(drawingFile));
            }
            return drawingFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("ResultActivity", "Error saving unique drawing", e);
            return null;
        }
    }
    /**
     * Converts the entire result view into a shareable image file.
     */
    private File createShareableImage() {
        try {
            // Prepare a bitmap for the entire result layout
            View view = findViewById(R.id.resultContainer); // Entire result view
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            // Save bitmap to a file
            File sharedDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shared_results");
            if (!sharedDir.exists()) sharedDir.mkdirs();

            File sharedFile = new File(sharedDir, "result_" + System.currentTimeMillis() + ".png");
            FileOutputStream outputStream = new FileOutputStream(sharedFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return sharedFile;
        } catch (Exception e) {
            Log.e("ResultActivity", "Error creating shareable image", e);
            return null;
        }
    }

    /**
     * Shares the generated image file via an Intent.
     */
    private void shareResult(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my test result from DrawYourMind!");
        startActivity(Intent.createChooser(shareIntent, "Share your result via"));
    }
}
