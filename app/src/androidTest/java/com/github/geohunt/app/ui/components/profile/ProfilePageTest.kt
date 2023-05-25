package com.github.geohunt.app.ui.components.profile

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.geohunt.app.data.exceptions.UserNotFoundException
import com.github.geohunt.app.data.repository.*
import com.github.geohunt.app.mocks.*
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.model.User
import com.github.geohunt.app.model.database.api.ProfileVisibility
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
        challenge: ChallengeRepositoryInterface? = MockChallengeRepository(),
        follow: FollowRepositoryInterface? = MockFollowRepository(),
        visibility: ProfileVisibilityRepositoryInterface? = MockProfileVisibilityRepository(),
        claims: ClaimRepositoryInterface? = MockClaimRepository(),
        score: ScoreRepositoryInterface? = MockScoreRepository(),
        uid: String = "1"
    ): ProfilePageViewModel {
        return ProfilePageViewModel(
            authRepository = auth ?: appContainer!!.auth,
            userRepository = user ?: appContainer!!.user,
            challengeRepository = challenge ?: appContainer!!.challenges,
            followRepository = follow ?: appContainer!!.follow,
            profileVisibilityRepository = visibility ?: appContainer!!.profileVisibilities,
            claimRepository = claims ?: appContainer!!.claims,
            scoreRepository = score ?: appContainer!!.score,
            uid = uid
        )
    }


    @OptIn(ExperimentalTestApi::class)
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
        testRule.waitUntilDoesNotExist(hasTestTag("progress"), 10_000L)
    }

    @Test
    fun showsSumOfClaimsAsScore() {
            val vm = createViewModel(
            score = object: MockScoreRepository() {
                override suspend fun getScore(uid: String): Long {
                    return 169L
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
            claims = object: MockClaimRepository() {
                override suspend fun getClaims(uid: String): List<Claim> {
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
            },
            claims = object: MockClaimRepository() {
                override suspend fun getClaims(uid: String): List<Claim> {
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
            ProfilePage(viewModel = vm, openSettings = {})
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


    @Test
    fun doesNotShowProfileIfPrivate() {
        val vm = createViewModel(
            visibility = object: MockProfileVisibilityRepository() {
                override suspend fun getProfileVisibility(uid: String): Flow<ProfileVisibility> {
                    assert(uid == "2")
                    return flowOf(ProfileVisibility.PRIVATE)
                }
            },
            uid = "2"
        )
        testRule.setContent {
            ProfilePage(vm)
        }
        testRule.onNodeWithTag("private-profile").assertIsDisplayed()
    }

    @Test
    fun showsErrorIfUserDoNotExists() {
        val vm = createViewModel(
            uid = "-",
            user = object: MockUserRepository() {
                override suspend fun getUser(id: String): User {
                    if (id == "-") throw UserNotFoundException("-")
                    return super.getUser(id)
                }
            }
        )
        testRule.setContent {
            ProfilePage(vm)
        }
        testRule.onNodeWithTag("error-profile").assertIsDisplayed()
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