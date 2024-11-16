package com.example.darwyourmind.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // JSON 파일을 통해 카테고리 리스트 설정
        categoryList = JSONHelper.loadCategories(this);

        // 어댑터에 카테고리 리스트를 전달하고 RecyclerView에 설정
        adapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu); // 'home_menu.xml'로 변경
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("MenuDebug", "Clicked item ID: " + id); // 디버깅 로그 추가

        if (id == R.id.action_about_us) {
            openAboutUs();
            return true;
        } else if (id == R.id.action_help) {
            openHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    private void openHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

}
