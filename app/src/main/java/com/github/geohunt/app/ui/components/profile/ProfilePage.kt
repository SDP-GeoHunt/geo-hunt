package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.model.database.firebase.FirebaseDatabase
import com.github.geohunt.app.model.database.firebase.FirebaseUserRef
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.ui.rememberLazyRef
import com.github.geohunt.app.utility.findActivity

@Composable
fun ProfilePage(id: String) {
    ProfilePage(user = FirebaseUserRef(id, FirebaseDatabase(LocalContext.current.findActivity())))
}

@Composable
fun ProfilePage(user: LazyRef<User>) {
    val lazyRefRemember = rememberLazyRef { user }

    Box(modifier = Modifier.fillMaxSize().fillMaxWidth()) {
        if (lazyRefRemember.value == null) {
            CircularProgressIndicator()
        } else {
            ProfilePageContent(lazyRefRemember.value!!)
        }
    }
}

@Composable
private fun ProfilePageContent(user: User) {
    Column {
        Row {
            ProfileIcon(user = user)

            Column {
                Text(user.displayName ?: user.uid)

                Row {
                    BigNumberWithText(
                        title = user.challenges.size.toString(),
                        subtitle = stringResource(id = R.string.profile_number_of_posts_subtitle)
                    )
                    BigNumberWithText(
                        title = user.hunts.size.toString(),
                        subtitle = stringResource(id = R.string.profile_number_of_hunts_subtitle)
                    )
                    BigNumberWithText(
                        title = "#1",
                        subtitle = stringResource(id = R.string.profile_number_of_ranking_subtitle)
                    )
                }
            }
        }

        PastChallengeAndHunts(user)
    }
}

@Composable
private fun BigNumberWithText(title: String, subtitle: String) {
    Column {
        Text(title)
        Text(subtitle)
    }
}