package com.example.darwyourmind.utils;

import android.content.Context;

import com.example.darwyourmind.R;
import com.example.darwyourmind.models.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONHelper {

    public static List<Category> loadCategories(Context context) {
        List<Category> categories = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.questions);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray categoriesArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                Category category = new Category();
                category.setName(categoryObject.getString("name"));
                category.setDrawingPrompt(categoryObject.getString("drawing_prompt"));

                JSONArray questionsArray = categoryObject.getJSONArray("questions");
                List<String> questions = new ArrayList<>();
                for (int j = 0; j < questionsArray.length(); j++) {
                    questions.add(questionsArray.getString(j));
                }
                category.setQuestions(questions);
                categories.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
}