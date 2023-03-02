package com.github.geohunt.app.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.geohunt.app.R;

import com.github.geohunt.app.presentation.BoredViewModel;

public class MainActivity extends AppCompatActivity {
    BoredViewModel boredViewModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boredViewModel = new BoredViewModel(getApplicationContext());

        // Update the activity text on each data update
        TextView activityText = findViewById(R.id.boredResponse);
        boredViewModel.getCurrentActivity().observe(this, boredActivity -> {
            if (boredActivity != null) {
                activityText.setText(String.format("You could try this%s activity: %s",
                        boredViewModel.isCached() ? " (cached)" : "",
                        boredActivity.getActivity())
                );
            } else {
                activityText.setText("I could not retrieve an activity for you :(");
            }
        });
    }

    public void onValidate(View view) {
        // Retrieve the name entered by the user
        EditText mainName = findViewById(R.id.mainName);

        // Create the greeting activity intent
        Intent launchGreeting = new Intent(this, GreetingActivity.class);
        launchGreeting.putExtra("name", mainName.getText().toString());
        startActivity(launchGreeting);
    }

    /**
     * Gets a new, fresh activity when the button is clicked.
     * @param view The current view object.
     */
    public void onBoredClick(View view) {
        boredViewModel.getActivity();
    }
}