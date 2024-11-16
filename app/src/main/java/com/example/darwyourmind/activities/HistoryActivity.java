package com.example.darwyourmind.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.darwyourmind.R;
import com.example.darwyourmind.adapters.HistoryAdapter;
import com.example.darwyourmind.adapters.TestHistoryManager;

import org.json.JSONObject;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private TestHistoryManager historyManager;
    private Button clearHistoryButton; // 추가 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTitle("Test History");

        // 히스토리 매니저 초기화
        historyManager = new TestHistoryManager(this);

        // 히스토리 데이터 가져오기
        List<JSONObject> historyList = historyManager.getTestHistory();

        // RecyclerView 초기화
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        historyAdapter = new HistoryAdapter(historyList, this);
        historyRecyclerView.setAdapter(historyAdapter);

        // Clear History 버튼 초기화
        clearHistoryButton = findViewById(R.id.clearHistoryButton);
        clearHistoryButton.setOnClickListener(v -> {
            historyManager.clearHistory(); // 히스토리 초기화
            historyList.clear();          // RecyclerView 업데이트를 위해 리스트도 초기화
            historyAdapter.notifyDataSetChanged();
            Toast.makeText(this, "History cleared!", Toast.LENGTH_SHORT).show();
        });
    }
}
