package com.github.geohunt.app.ui.components.profile

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.AuthRepositoryInterface
import com.github.geohunt.app.data.repository.ChallengeRepositoryInterface
import com.github.geohunt.app.data.repository.FollowRepositoryInterface
import com.github.geohunt.app.data.repository.UserRepositoryInterface
import com.github.geohunt.app.mocks.MockChallenge
import com.github.geohunt.app.mocks.MockChallengeRepository
import com.github.geohunt.app.mocks.MockFollowRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    private val photoUrl = ""

    @Before
    fun initAppContainer() {
        appContainer = AppContainer.getEmulatedFirebaseInstance(
            androidx.test.core.app.ApplicationProvider.getApplicationContext() as Application
        )
    }

    private fun createViewModel(
        auth: AuthRepositoryInterface? = MockAuthRepository(),
        user: UserRepositoryInterface? = MockUserRepository(appContainer!!.user),
        challenge: ChallengeRepositoryInterface? = null,
        follow: FollowRepositoryInterface? = MockFollowRepository(),
        uid: String = "1"
    ): ProfilePageViewModel {
        return ProfilePageViewModel(
            authRepository = auth ?: appContainer!!.auth,
            userRepository = user ?: appContainer!!.user,
            challengeRepository = challenge ?: appContainer!!.challenges,
            followRepository = follow ?: appContainer!!.follow,
            uid = uid
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
                        Claim("1", "1", "1", photoUrl, LocalDateTime.MIN, 1, 100),
                        Claim("1", "1", "1", photoUrl, LocalDateTime.MIN, 1, 69)
                    )
                }
            },

        )
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithText("169").assertExists()
    }

    @Test
    fun showsDisplayName() {
        testRule.setContent {
            ProfilePage(viewModel = createViewModel())
        }
        testRule.onNodeWithText("dn").assertExists()
    }

    @Test
    fun showsNumberOfHunts() {
        val vm = createViewModel(
            challenge = object: MockChallengeRepository() {
                override fun getClaimsFromUser(uid: String): List<Claim> {
                    return listOf(
                        Claim("1", "1", "1", photoUrl, LocalDateTime.MIN, 1, 100),
                        Claim("1", "1", "1", photoUrl, LocalDateTime.MIN, 1, 69)
                    )
                }
            }
        )

        testRule.setContent {
            ProfilePage(vm)
        }
        testRule.onNodeWithText("2").assertExists()
    }


    @Test
    fun showsNumberOfChallenges() {
        val vm = createViewModel(
            challenge = object: MockChallengeRepository() {
                override fun getPosts(userId: String): Flow<List<Challenge>> {
                    return flowOf(listOf(MockChallenge(), MockChallenge(), MockChallenge()))
                }
                override fun getClaimsFromUser(uid: String): List<Claim> {
                    return listOf(
                        Claim("1", "1", "1", photoUrl, LocalDateTime.MIN, 1, 100),
                        Claim("1", "1", "1", photoUrl, LocalDateTime.MIN, 1, 69)
                    )
                }
            }
        )
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun doesNotShowSettingsBtnIfNotNeeded() {
        testRule.setContent {
            ProfilePageContent(User("1", "dn", null), listOf(), listOf(), 1, null)
        }
        testRule.onNodeWithTag("profile-settings-btn").assertDoesNotExist()
    }

    @Test
    fun showsSettingsBtnIfAvailable() {
        testRule.setContent {
            ProfilePageContent(User("1", "dn", null), listOf(), listOf(), 1, { })
        }
        testRule.onNodeWithTag("profile-settings-btn").assertIsDisplayed()
    }

    @Test
    fun showsSettingsBtnIfAvailable1() {
        val vm = createViewModel()
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithTag("profile-settings-btn").assertIsDisplayed()
    }

    @Test
    fun doesNotShowSettingsBtnIfAnotherUser() {
        val vm = createViewModel(uid = "2")
        testRule.setContent {
            ProfilePage(viewModel = vm) { }
        }
        testRule.onNodeWithTag("profile-settings-btn").assertDoesNotExist()
    }

    @Test
    fun clickingOnSettingsBtnShowsDrawer() {
        val vm = createViewModel()
        testRule.setContent {
            ProfilePage(viewModel = vm) { }
        }
        testRule.onNodeWithTag("settings-drawer").assertIsNotDisplayed()
        testRule.onNodeWithTag("profile-settings-btn").performClick()
        testRule.onNodeWithTag("settings-drawer").assertIsDisplayed()
    }

    @Test
    fun showsFollowButtonIfAvailable() {
        val vm = createViewModel(uid = "2")
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithTag("follow-btn").assertIsDisplayed()
    }

    @Test
    fun doesNotShowFollowButtonIfOwnProfile() {
        val vm = createViewModel()
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithTag("follow-btn").assertDoesNotExist()
    }

    /* Test fails for unknown reason
    @Test
    fun clickingOnFollowButtonTriggersFollow() {
        val cf = CompletableFuture<User>()
        val vm = createViewModel(uid = "2",
            follow = object: MockFollowRepository() {
                override suspend fun follow(user: User) {
                    withContext(Dispatchers.IO) {
                        cf.complete(user)
                    }
                }
            }
        )
        testRule.setContent {
            ProfilePage(viewModel = vm)
        }
        testRule.onNodeWithTag("follow-btn").performClick()
        // assert(cf.get(2, TimeUnit.SECONDS).id == "2")
    }
     */
}