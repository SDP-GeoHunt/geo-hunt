package com.github.geohunt.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onValidateClicked(View view) {
        // Retrieve the name entered by the user
        EditText mainName = findViewById(R.id.mainName);

        // Create the greeting activity intent
        Intent launchGreeting = new Intent(this, GreetingActivity.class);
        launchGreeting.putExtra("name", mainName.getText().toString());
        startActivity(launchGreeting);
    }

    public void onDatabaseClicked(View view) {
        // Launch the database activity intent
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }
}