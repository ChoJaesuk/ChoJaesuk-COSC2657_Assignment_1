package com.example.darwyourmind.activities;

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // JSON 파일을 통해 카테고리 리스트 설정
        categoryList = JSONHelper.loadCategories(this);

        // 어댑터에 카테고리 리스트를 전달하고 RecyclerView에 설정
        adapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(adapter);
    }
}
