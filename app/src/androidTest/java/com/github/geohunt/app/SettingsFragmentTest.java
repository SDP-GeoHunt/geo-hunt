package com.github.geohunt.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.github.geohunt.app.ui.settings.SettingsFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsFragmentTest {

    @Rule
    public FragmentTestRule<?, SettingsFragment> fragmentTestRule = FragmentTestRule.create(SettingsFragment.class);

    @Test
    public void testSettingsFragmentDisplayedCorrectly() {
        onView(withId(R.id.text_settings)).check(matches(isDisplayed()));
    }

}
