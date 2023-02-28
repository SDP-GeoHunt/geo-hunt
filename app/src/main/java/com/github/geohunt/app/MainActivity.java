package com.github.geohunt.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.geohunt.app.api.BoredActivityData;
import com.github.geohunt.app.api.BoredApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://www.boredapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    final BoredApi boredApi = retrofit.create(BoredApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onValidate(View view) {
        // Retrieve the name entered by the user
        EditText mainName = findViewById(R.id.mainName);

        // Create the greeting activity intent
        Intent launchGreeting = new Intent(this, GreetingActivity.class);
        launchGreeting.putExtra("name", mainName.getText().toString());
        startActivity(launchGreeting);
    }

    public void onBoredClick(View view) {
        TextView textView = findViewById(R.id.boredResponse);
        boredApi.getActivity().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<BoredActivityData> call, @NonNull Response<BoredActivityData> response) {
                assert response.body() != null;
                textView.setText(String.format("You could try this activity: %s", response.body().getActivity()));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<BoredActivityData> call, @NonNull Throwable t) {
                textView.setText("Error: could not retrieve activity.");
            }
        });
    }
}