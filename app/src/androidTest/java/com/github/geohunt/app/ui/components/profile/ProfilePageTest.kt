package com.github.geohunt.app.ui.components.profile

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.FollowRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockFollowRepository
import com.github.geohunt.app.mocks.MockUser
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

class ProfilePageTest {
    @get:Rule
    val testRule = createComposeRule()

    var appContainer: AppContainer? = null

    @Before
    fun initAppContainer() {
        appContainer = AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    fun createViewModel(
        auth: AuthRepositoryInterface? = MockAuthRepository(),
        user: UserRepositoryInterface? = MockUserRepository(appContainer!!.user),
        challenge: ChallengeRepositoryInterface? = null,
        follow: FollowRepositoryInterface? = MockFollowRepository()
    ): ProfilePageViewModel {
        return ProfilePageViewModel(
            authRepository = auth ?: appContainer!!.auth,
            userRepository = user ?: appContainer!!.user,
            challengeRepository = challenge ?: appContainer!!.challenges,
            followRepository = follow ?: appContainer!!.follow
        )
    }


    @Test
    fun showsLoadingIfNotReady() {
        val cfCanReturn = CompletableFuture<Unit>()
        val vm = createViewModel(
            user = object: MockUserRepository(appContainer!!.user) {
                override suspend fun getUser(id: String): User {
                    return withContext(Dispatchers.IO) {
                        cfCanReturn.get() // Blocking is intended
                        User("1", "dn", null)
                    }
                }
            }
        )
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }

        testRule.onNodeWithTag("progress").assertIsDisplayed()
        cfCanReturn.complete(null)
        testRule.onNodeWithTag("progress").assertDoesNotExist()
    }

    @Test
    fun showsSumOfClaimsAsScore() {
        val vm = createViewModel(
            challenge = object: MockChallengeRepository() {
                override fun getClaimsFromUser(uid: String): List<Claim> {
                    return listOf(
                        Claim("1", "1", "1", LocalDateTime.MIN, 1, 100),
                        Claim("1", "1", "1", LocalDateTime.MIN, 1, 69)
                    )
                }
            },

        )
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithText("169").assertExists()
    }

    /*@Test
    fun showsDisplayName() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser(displayName = "coucou")))
        }
        testRule.onNodeWithText("coucou").assertExists()
    }


    @Test
    fun showsNumberOfHunts() {
        val mockuser = MockUser(hunts = listOf(
            wrapLazyChallenge(MockChallengeClass()),
            wrapLazyChallenge(MockChallengeClass()),
            wrapLazyChallenge(MockChallengeClass())
        ))

        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", mockuser) )
        }
        testRule.onNodeWithText(mockuser.challenges.size.toString()).assertExists()
    }

    @Test
    fun showsNumberOfChallenges() {
        val mockuser = MockUser(challenges = listOf(
            wrapLazyChallenge(MockChallengeClass()),
            wrapLazyChallenge(MockChallengeClass()),
            wrapLazyChallenge(MockChallengeClass())
        ))
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", mockuser))
        }
        testRule.onNodeWithText(mockuser.challenges.size.toString()).assertExists()
    }

    private fun wrapLazyChallenge(challenge: Challenge): LazyRef<Challenge> {
        return MockLazyRef("1") { Tasks.forResult(challenge) }
    }

    @Test
    fun doesNotShowSettingsBtnIfNotNeeded() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()))
        }
        testRule.onNodeWithTag("profile-settings-btn").assertDoesNotExist()
    }

    @Test
    fun showsSettingsBtnIfAvailable1() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertExists()
    }

    @Test
    fun showsSettingsBtnIfAvailable2() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), null, { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertExists()
    }

    @Test
    fun showsSettingsBtnIfAvailable3() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), null, null, { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertExists()
    }

    @Test
    fun clickingOnSettingsBtnShowsDrawer() {
        testRule.setContent {
            ProfilePage(user = InstantLazyRef("1", MockUser()), { })
        }
        testRule.onNodeWithTag("settings-drawer").assertIsNotDisplayed()
        testRule.onNodeWithTag("profile-settings-btn").performClick()
        testRule.onNodeWithTag("settings-drawer").assertIsDisplayed()
    }*/
}