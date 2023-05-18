package com.github.geohunt.app.ui.components.bounties

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.github.geohunt.app.data.repository.AppContainer
import com.github.geohunt.app.data.repository.ImageRepository
import com.github.geohunt.app.data.repository.bounties.BountiesRepository
import com.github.geohunt.app.mocks.MockAuthRepository
import com.github.geohunt.app.mocks.MockBountyRepository
import com.github.geohunt.app.mocks.MockUserRepository
import com.github.geohunt.app.model.database.FirebaseEmulator
import com.github.geohunt.app.ui.components.bounties.viewmodel.AdminBountyViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestBountyManageView {
    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setup() {
        AppContainer.getEmulatedFirebaseInstance(
            ApplicationProvider.getApplicationContext() as Application
        )
    }

    @Test
    fun testBountyManagePage() = runTest {
        testRule.setContent {
            AdminBountyPage(
                bid = "1",
                viewModel = AdminBountyViewModel(
                    userRepository = MockUserRepository(),
                    bountiesRepository = MockBountyRepository(),
                )
            )
        }

        testRule.waitUntil(50000) {
            testRule.onAllNodesWithTag("admin-bounty-page-loaded")
                .fetchSemanticsNodes().size == 1
        }

        testRule.onNodeWithText("<Bounty-Name>")
            .assertIsDisplayed()

        testRule.onNodeWithText("<Team Name>")
    }

    @Test
    fun testBountyRename() = runTest {
        val bountiesRepository = MockBountyRepository()

        testRule.setContent {
            AdminBountyPage(
                bid = "1",
                viewModel = AdminBountyViewModel(
                    userRepository = MockUserRepository(),
                    bountiesRepository = bountiesRepository,
                )
            )
        }

        testRule.waitUntil(50000) {
            testRule.onAllNodesWithTag("admin-bounty-page-loaded")
                .fetchSemanticsNodes().size == 1
        }

        testRule.onNodeWithTag("edit-btn")
            .assertIsDisplayed()
            .performClick()

        testRule.awaitIdle()

        testRule.onNodeWithTag("rename-field")
            .performTextClearance()

        testRule.onNodeWithTag("rename-field")
            .performTextInput("<New-Bounty-Name>")

        testRule.onNodeWithText("Ok")
            .performClick()

        assertThat(bountiesRepository.name, equalTo("<New-Bounty-Name>"))
    }


}