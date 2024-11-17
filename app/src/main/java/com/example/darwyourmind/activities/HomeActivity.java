package com.example.darwyourmind.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.darwyourmind.R;
import com.example.darwyourmind.adapters.CategoryAdapter;
import com.example.darwyourmind.models.Category;
import com.example.darwyourmind.utils.JSONHelper;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Home");

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = JSONHelper.loadCategories(this);
        adapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(adapter);

        // About Us 버튼 클릭 이벤트
        findViewById(R.id.aboutUsButton).setOnClickListener(v -> openAboutUs());

        // Help 버튼 클릭 이벤트
        findViewById(R.id.helpButton).setOnClickListener(v -> openHelp());

        // History 버튼 클릭 이벤트
        findViewById(R.id.historyButton).setOnClickListener(v -> openHistory());
    }

    private void openAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void openHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void openHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
