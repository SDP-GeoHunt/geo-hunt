package com.github.geohunt.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String USERNAME = "BingChilling";

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void onTestStart() {
        Intents.init();
    }

    @Before
    public void onTestEnd() {
        Intents.release();
    }

    @Test
    public void mainActivityStartGreetingUponClick() {
        // write the username to the corresponding field
        onView(withId(R.id.mainName))
                .perform(typeText(USERNAME));

        // click on the navigate button. Notice that without
        // the closeSoftKeyboard() call this test won't pass
        // the CI. Likely due to different screen behavior
        onView(withId(R.id.mainGoButton))
                .perform(closeSoftKeyboard())
                .perform(click());

        // ensure that a new intent as been emitted
        intended(allOf(
                IntentMatchers.hasComponent(GreetingActivity.class.getName()),
                IntentMatchers.hasExtra("name", USERNAME)
        ));
    }

    @Test
    public void mainActivityStartDatabaseUponClick() {
        // click on the database button
        onView(withId(R.id.mainDatabaseButton))
                .perform(closeSoftKeyboard())
                .perform(click());

        // ensure that a new intent as been emitted
        intended(allOf(
                IntentMatchers.hasComponent(DatabaseActivity.class.getName())
        ));
    }
}
