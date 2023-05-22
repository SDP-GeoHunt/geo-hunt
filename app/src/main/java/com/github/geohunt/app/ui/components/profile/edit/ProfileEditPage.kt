package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.components.appbar.TopAppBarWithBackButton

@Composable
fun ProfileEditPage(onBackButton: () -> Unit, vm: ProfileEditPageViewModel = viewModel(factory = ProfileEditPageViewModel.Factory)) {
    // Getting user
    val user by vm.user.collectAsState()
    val eu by vm.editedUser.collectAsState()
    val isSaving by vm.isUpdating.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                onBack = onBackButton,
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
