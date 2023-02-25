package com.github.geohunt.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Intent;
import android.provider.ContactsContract;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.geohunt.app.database.Databases;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@RunWith(AndroidJUnit4.class)
public class DatabaseActivityTest {
    @Rule
    public ActivityScenarioRule<DatabaseActivity> rule = new ActivityScenarioRule<>(DatabaseActivity.class);

    private static final String BILLY_PHONE_NUMBER = "4515166187";
    private static final String BILLY_EMAIL = "billy@gmail.com";

    private static final String MARC_PHONE_NUMBER = "999";
    private static final String MARC_EMAIL = "marc.antoine@roman.it";

    @Test
    public void testSetRequestProperlyWork() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Runnable> terminated = new CompletableFuture<>();

        Databases.setInstance(new MockDatabase(
                (path, clss) -> {
                    throw new IllegalStateException();
                },
                (path, obj) -> {
                    CompletableFuture<Void> completableFuture = new CompletableFuture<>();
                    completableFuture.complete(null);
                    terminated.complete(() -> {
                        assertThat(path, equalTo("/" + MARC_PHONE_NUMBER));
                        assertThat(obj, is(instanceOf(String.class)));
                        assertThat((String) obj, equalTo(MARC_EMAIL));
                    });
                    return completableFuture;
                }
        ));

        // Write marc's phone and email
        onView(withId(R.id.PhoneField))
                .perform(typeText(MARC_PHONE_NUMBER));

        onView(withId(R.id.EmailField))
                .perform(closeSoftKeyboard())
                .perform(typeText(MARC_EMAIL));

        // Perform the set request
        onView(withId(R.id.SetButton))
                .perform(closeSoftKeyboard())
                .perform(click());

        // Wait until the end of the test
        terminated.get(10, TimeUnit.SECONDS).run();
    }

    @Test
    public void testGetRequestWithValidPhone() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Runnable> terminated = new CompletableFuture<>();

        Databases.setInstance(new MockDatabase(
                (path, clss) -> { // Get requests
                    CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                    completableFuture.complete(BILLY_EMAIL);
                    terminated.complete(() -> {
                        assertThat(clss, equalTo(String.class));
                        assertThat(path, equalTo("/" + BILLY_PHONE_NUMBER));
                    });
                    return completableFuture;
                },
                (path, obj) -> {
                    throw new IllegalStateException();
                }
        ));

        // Write billy's phone number
        onView(withId(R.id.PhoneField))
                .perform(typeText(BILLY_PHONE_NUMBER));

        // Perform the get request
        onView(withId(R.id.GetButton))
                .perform(closeSoftKeyboard())
                .perform(click());

        // Wait until the end of the test
        terminated.get(10, TimeUnit.SECONDS).run();
    }



}
