package com.example.darwyourmind.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TestHistoryManager {

    private static final String PREF_NAME = "TestHistory";
    private static final String KEY_HISTORY = "history";

    private SharedPreferences sharedPreferences;

    public TestHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTestResult(String title, String drawingPath, ArrayList<String[]> results) {
        try {
            if (title == null || title.isEmpty()) {
                Log.e("TestHistoryManager", "Title is missing.");
                return;
            }

            if (results == null || results.isEmpty()) {
                Log.e("TestHistoryManager", "Results are missing.");
                return;
            }

            // Existing history
            String historyString = sharedPreferences.getString(KEY_HISTORY, "[]");
            JSONArray historyArray = new JSONArray(historyString);

            // New test result
            JSONObject testResult = new JSONObject();
            testResult.put("title", title);
            testResult.put("drawingPath", drawingPath != null ? drawingPath : ""); // Default empty if null

            JSONArray resultArray = new JSONArray();
            for (String[] result : results) {
                JSONObject resultObj = new JSONObject();
                resultObj.put("question", result[0]);
                resultObj.put("answer", result[1]);
                resultObj.put("explanation", result[2]);
                resultArray.put(resultObj);
            }
            testResult.put("results", resultArray);

            historyArray.put(testResult);

            // Save to SharedPreferences
            sharedPreferences.edit().putString(KEY_HISTORY, historyArray.toString()).apply();
            Log.d("TestHistoryManager", "Test result saved successfully.");
        } catch (Exception e) {
            Log.e("TestHistoryManager", "Error saving test result", e);
        }
    }


    public List<JSONObject> getTestHistory() {
        List<JSONObject> historyList = new ArrayList<>();
        try {
            String historyString = sharedPreferences.getString(KEY_HISTORY, "[]");
            JSONArray historyArray = new JSONArray(historyString);

            for (int i = 0; i < historyArray.length(); i++) {
                historyList.add(historyArray.getJSONObject(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyList;
    }

    public void clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply();
    }
}

