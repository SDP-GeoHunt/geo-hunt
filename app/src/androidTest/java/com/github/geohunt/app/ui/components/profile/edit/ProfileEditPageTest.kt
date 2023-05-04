package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.EditedUser
import com.github.geohunt.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class ProfileEditPageTest {
    @get:Rule
    val c = createComposeRule()

    private fun createViewModel(authRepository: MockAuthRepository = MockAuthRepository(), userRepository: MockUserRepository = MockUserRepository()): ProfileEditPageViewModel {
        return ProfileEditPageViewModel(
            authRepository,
            userRepository
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun showsLoadingIfNotReadyYet() {
        val cf = CompletableFuture<Void?>()
        val vm = createViewModel(
            userRepository = object: MockUserRepository() {
                override suspend fun getCurrentUser(): User {
                    return withContext(Dispatchers.IO) {
                        cf.get()
                        MockAuthRepository.defaultLoggedUser
                    }
                }
            }
        )
        c.setContent {
            ProfileEditPage(onBackButton = { }, vm = vm)
        }
        c.onNodeWithTag("progress").assertIsDisplayed()
        cf.complete(null)
        c.waitUntilDoesNotExist(hasTestTag("progress"), 10_000L)
    }

    @Test
    fun titleIsShown() {
        c.setContent { ProfileEditPage({ }, vm = createViewModel()) }
        c.onNodeWithText("Edit profile").assertIsDisplayed()
    }

    @Test
    fun triggersUpdateCorrectly() {
        val cf = CompletableFuture<EditedUser>()
        val vm = createViewModel(
            userRepository = object: MockUserRepository() {
                override suspend fun updateUser(editedUser: EditedUser) {
                    cf.complete(editedUser)
                }
            }
        )
        c.setContent {
            ProfileEditPage(onBackButton = {}, vm = vm)
        }
        c.onNodeWithTag("display-name-input").performTextInput("prout")
        c.onNodeWithTag("save-btn").performClick()
        val newUser = cf.get(2, TimeUnit.SECONDS)
        assert(newUser.newDisplayName != null)
        assert(newUser.newDisplayName!!.contains("prout"))
    }

    /*
    @Before
    fun mockAuthenticator() {
        Authenticator.authInstance.set(LoginActivityTest.MockAuthenticator(MockUser(uid = "1")))
    }



    @Test
    fun clickingOnBackButtonTriggersCallback() {
        val cf = CompletableFuture<Void?>()
        c.setContent { ProfileEditPage {
            cf.complete(null)
        }}
        c.onNodeWithTag("back-btn").performClick()
        cf.get(2, TimeUnit.SECONDS)
    }*/
    // TODO
}