package com.example.darwyourmind.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.darwyourmind.R;

import java.io.File;
import java.util.ArrayList;

public class ResultActivity extends BaseActivity  {

    private ImageView drawingImageView;
    private LinearLayout resultContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        drawingImageView = findViewById(R.id.drawingImageView);
        resultContainer = findViewById(R.id.resultContainer);

        // Set title for the ActionBar
        setTitle("Result");

        // Load the drawing image
        String drawingPath = getIntent().getStringExtra("drawingPath");
        if (drawingPath != null) {
            Bitmap bitmap = loadImageFromPath(drawingPath);
            if (bitmap != null) {
                drawingImageView.setImageBitmap(bitmap);
            } else {
                Log.e("ResultActivity", "Failed to load drawing image.");
            }
        }

        // Get result list from intent
        ArrayList<String[]> resultList = (ArrayList<String[]>) getIntent().getSerializableExtra("resultList");

        // Add each question, answer, and explanation as a card
        for (String[] result : resultList) {
            addResultCard(result[0], result[1], result[2]);
        }
    }


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

    private Bitmap loadImageFromPath(String path) {
        try {
            File file = new File(path);
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
