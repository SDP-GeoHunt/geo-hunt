package com.github.geohunt.app.ui.screens.bounty

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ViewBountyPage(
    bountyId: String,
    onBack: () -> Any,
    viewModel: ViewBountyViewModel = viewModel(factory = ViewBountyViewModel.getFactory(bountyId))
) {

}