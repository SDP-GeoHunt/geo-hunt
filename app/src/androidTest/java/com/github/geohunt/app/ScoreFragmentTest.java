package com.github.geohunt.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.github.geohunt.app.ui.score.ScoreFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScoreFragmentTest {

    @Rule
    public FragmentTestRule<?, ScoreFragment> fragmentTestRule = FragmentTestRule.create(ScoreFragment.class);

    @Test
    public void testScoreFragmentDisplayedCorrectly() {
        onView(withId(R.id.text_score)).check(matches(isDisplayed()));
    }
}
