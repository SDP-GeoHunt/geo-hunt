package com.github.geohunt.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        // Personalize the greeting message
        Intent thisIntent = getIntent();
        TextView textView = findViewById(R.id.greetingMessage);
        String name = getIntent().getStringExtra("name");
        textView.setText(String.format("Hello %s!", name));
    }
}