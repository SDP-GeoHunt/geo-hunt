package com.github.geohunt.app.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

    Box(modifier = Modifier
        .fillMaxSize()
        .fillMaxWidth()) {
        if (lazyRefRemember.value == null) {
            Progress()
        } else {
            ProfilePageContent(lazyRefRemember.value!!)
        }
    }
}

@Composable
@NoLiveLiterals // Without this, tests do not passes (issue with Jetpack Compose)
private fun ProfilePageContent(user: User) {
    Column {
        Row {
            ProfileIcon(user = user)

            Column(modifier = Modifier.padding(0.dp, 12.dp)) {
                Text(user.displayName ?: user.uid)

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {
                    BigNumberWithText(
                        title = user.challenges.size.toString(),
                        subtitle = stringResource(id = R.string.profile_number_of_posts_subtitle)
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    BigNumberWithText(
                        title = user.hunts.size.toString(),
                        subtitle = stringResource(id = R.string.profile_number_of_hunts_subtitle)
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    BigNumberWithText(
                        title = user.score.toString(),
                        subtitle = stringResource(id = R.string.profile_score_subtitle)
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
        Text(title, textAlign = TextAlign.Center, style = MaterialTheme.typography.h1)
        Text(subtitle, textAlign = TextAlign.Center, style = MaterialTheme.typography.h3)
    }
}

@Composable
@NoLiveLiterals // Without this, tests do not passes (issue with Jetpack Compose)
private fun Progress() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(modifier = Modifier.testTag("progress"))
    }
}