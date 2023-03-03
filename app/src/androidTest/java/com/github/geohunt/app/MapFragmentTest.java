package com.github.geohunt.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.github.geohunt.app.ui.map.MapFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class MapFragmentTest {

    @Rule
    public FragmentTestRule<?, MapFragment> fragmentTestRule = FragmentTestRule.create(MapFragment.class);

    @Test
    public void testMapFragmentDisplayedCorrectly() {
        onView(withId(R.id.text_map)).check(matches(isDisplayed()));
    }
}
