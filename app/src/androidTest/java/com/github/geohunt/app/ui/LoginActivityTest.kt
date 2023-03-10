package com.github.geohunt.app.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.MainActivity
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.database.api.PictureImage
import com.github.geohunt.app.model.database.api.User
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @Test
    fun opensHomeActivityWhenLoggedIn() {
        launchActivity<LoginActivity>(Intent(ApplicationProvider.getApplicationContext(), LoginActivity::class.java)).onActivity {
            run {
                Intents.init()
                intended(allOf(IntentMatchers.hasComponent(MainActivity::class.java.name)))
            }
        }
    }

    class MockUser(
        override var displayName: String? = null,
        override val uid: String = "1",
        override val profilePicture: PictureImage? = null,
        override val challenges: List<String> = emptyList(),
        override val hunts: List<String> = emptyList(),
        override var score: Number = 1
    ) : User

    class MockAuthenticator(override val user: User?) : Authenticator {
        override fun authenticate(activity: ComponentActivity): CompletableFuture<User> {
            TODO("Not yet implemented")
        }

        override fun signOut(activity: ComponentActivity): CompletableFuture<Void> {
            TODO("Not yet implemented")
        }
    }
}