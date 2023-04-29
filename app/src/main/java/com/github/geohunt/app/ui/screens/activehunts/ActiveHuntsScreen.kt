package com.github.geohunt.app.ui.screens.activehunts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.ui.components.activehunts.ActiveHuntsList
import com.github.geohunt.app.ui.components.activehunts.ActiveHuntsTitle

/**
 * A screen that shows all the active hunts of a user.
 *
 * The hunts are displayed on a horizontal scrollable list.
 *
 * @param openExploreTab The function called to open the explore view in the navigation.
 * @param viewModel The [ActiveHuntsViewModel] related to this screen.
 *
 * @see [ActiveHuntsList]
 */
@Composable
fun ActiveHuntsScreen(
    openExploreTab: () -> Unit,
    viewModel: ActiveHuntsViewModel = viewModel(factory = ActiveHuntsViewModel.Factory)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        ActiveHuntsTitle()

        Spacer(modifier = Modifier.size(10.dp))

        val challenges = viewModel.activeHunts.collectAsStateWithLifecycle()

        when (challenges.value) {
            null -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else -> ActiveHuntsList(challenges.value!!, openExploreTab, getAuthorName = viewModel::getAuthorName)
        }
    }
}
