package com.github.geohunt.app;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.geohunt.app.view.GreetingActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {
    private static String USERNAME = "Here's Johny";

    @Test
    public void testGreetingActivityFromIntent()
    {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
        intent.putExtra("name", USERNAME);
        try (ActivityScenario<?> activity = ActivityScenario.launch(intent))
        {
            ViewInteraction text = onView(withId(R.id.greetingMessage));
            text.check(matches(withText(String.format("Hello %s!", USERNAME))));
        }
    }
}
