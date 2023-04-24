package com.github.geohunt.app.ui.components.settings.items

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.ProfileVisibility
import com.github.geohunt.app.ui.components.settings.RadioItem
import com.github.geohunt.app.ui.rememberLiveLazyRef

@Composable
fun LoggedUserContext.ProfileVisibilityChooser() {
    val userProfile = rememberLiveLazyRef { loggedUserRef }
    val profileVisibility = remember { mutableStateOf<ProfileVisibility?>(null) }
    val ctx = LocalContext.current

    LaunchedEffect(userProfile.value?.profileVisibility) {
        profileVisibility.value = userProfile.value?.profileVisibility
    }

    val failedUpdateStr = stringResource(id = R.string.update_profile_visibility_failed)
    LaunchedEffect(profileVisibility.value) {
        
        profileVisibility.value?.let {
            
            setProfileVisibility(it).addOnFailureListener {
                Toast.makeText(ctx, failedUpdateStr, Toast.LENGTH_LONG).show()
                Log.e("ERROR", "Could not update profile visibility $it")
            }
        }
    }

    Column {
        Text(stringResource(id = R.string.profile_visibility), style = MaterialTheme.typography.subtitle1)

        Column(modifier = Modifier.padding(16.dp)) {

            RadioItem(
                text = stringResource(id = R.string.public_word),
                subtext = stringResource(id = R.string.public_desc),
                state = profileVisibility,
                value = ProfileVisibility.PUBLIC
            )
            RadioItem(
                text = stringResource(id = R.string.follow_only),
                subtext = stringResource(id = R.string.follow_only_desc),
                state = profileVisibility,
                value = ProfileVisibility.FOLLOW_ONLY
            )
            RadioItem(
                text = stringResource(id = R.string.private_word),
                subtext = stringResource(id = R.string.private_desc),
                state = profileVisibility,
                value = ProfileVisibility.PRIVATE
            )
        }
    }
}
