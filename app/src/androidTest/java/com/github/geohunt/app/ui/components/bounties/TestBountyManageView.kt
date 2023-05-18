package com.github.geohunt.app.ui.components.bounties

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TestBountyManageView {
    @get:Rule
    val testRule = createComposeRule()

    @Before
    fun setup() {
        AppContainer.getEmulatedFirebaseInstance(
            ApplicationProvider.getApplicationContext() as Application
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
    }
}