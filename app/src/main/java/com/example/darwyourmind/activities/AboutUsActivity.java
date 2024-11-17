package com.example.darwyourmind.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.darwyourmind.R;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Set title for the ActionBar
        setTitle("About Us");

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Go back to the previous screen
        return true;
    }
}
