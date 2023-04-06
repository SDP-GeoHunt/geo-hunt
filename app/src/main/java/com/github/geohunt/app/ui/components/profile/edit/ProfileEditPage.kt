package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.authentication.Authenticator
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.components.navigation.TopBarWithBackButton
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.utility.findActivity

@Composable
fun ProfileEditPage(onBackButton: () -> Any) {
    // Getting user
    val uid = Authenticator.authInstance.get().user!!.uid
    val act = LocalContext.current.findActivity()
    val user = rememberLazyRef {
        Database.databaseFactory.get()(act).getUser(uid)
    }

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
            if (user.value == null) {
                Text("Loading") // TODO: replace
            } else {
                EditProfileContent(user.value!!)
            }
        }
    }
}
