package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton

@Composable
fun ProfileEditPage(onBackButton: () -> Any, vm: ProfileEditPageViewModel = viewModel(factory = ProfileEditPageViewModel.Factory)) {
    // Getting user
    val user by vm.user.collectAsState()
    val eu by vm.editedUser.collectAsState()
    val isSaving by vm.isUpdating.collectAsState()

    Scaffold(
        topBar = {
            TopBarWithBackButton(
                onBack = { onBackButton() },
                title = stringResource(id = R.string.edit_profile)
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .padding(16.dp)) {
            if (user == null || eu == null) {
                CircularProgressIndicator(modifier = Modifier
                    .fillMaxWidth()
                    .testTag("progress"))
            } else {
                EditProfileContent(user!!, eu!!, { vm.setDisplayName(it) }, { vm.setProfilePicture(it) }, isSaving, { vm.update() })
            }
        }
    }
}
