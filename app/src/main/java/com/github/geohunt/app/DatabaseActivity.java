package com.github.geohunt.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.geohunt.app.database.Databases;

import org.w3c.dom.Text;

public class DatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
    }

    public String getPhoneNumber() {
        TextView textView = findViewById(R.id.PhoneField);
        return textView.getText().toString();
    }

    public String getEmail() {
        TextView textView = findViewById(R.id.EmailField);
        return textView.getText().toString();
    }

    public void onGetClicked(View view) {
        // Post the phone-email couple to the database
        Databases.getInstance().root()
                .child(getPhoneNumber())
                .getRequest(String.class)
                .thenAccept(email -> {
                    this.<TextView>findViewById(R.id.EmailField)
                        .setText(email);
                });
    }

    public void onSetClicked(View view) {
        // Post the phone-email couple to the database
        Databases.getInstance().root()
                .child(getPhoneNumber())
                .postRequest(getEmail());
    }
}