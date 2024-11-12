package com.example.darwyourmind.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.darwyourmind.R;
import com.example.darwyourmind.adapters.QuestionAdapter;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView questionRecyclerView;
    private QuestionAdapter questionAdapter;
    private List<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionRecyclerView = findViewById(R.id.questionRecyclerView);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Intent로부터 질문 리스트 받기
        questions = getIntent().getStringArrayListExtra("questions");

        // 로그로 리스트 확인
        if (questions != null && !questions.isEmpty()) {
            Log.d("QuestionActivity", "questions 리스트: " + questions);
        } else {
            Log.e("QuestionActivity", "questions 리스트가 비어 있습니다.");
        }

        // Adapter에 리스트 설정 후 RecyclerView에 연결
        questionAdapter = new QuestionAdapter(questions);
        questionRecyclerView.setAdapter(questionAdapter);
    }

}

