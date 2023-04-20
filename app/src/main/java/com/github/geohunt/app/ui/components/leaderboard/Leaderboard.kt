package com.github.geohunt.app.ui.components.leaderboard

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.utility.findActivity
import com.github.geohunt.app.utility.thenDo
import com.github.geohunt.app.utility.thenMap
import com.github.geohunt.app.utility.toCompletableFuture
import com.google.android.gms.tasks.Tasks

/**
 * Creates a leaderboard view given a context
 *
 * The top 3 users are given special styling (see [LeaderboardPodiumItem]).
 *
 * @param users The users of the leaderboard, ranked by score.
 */
@Composable
fun LoggedUserContext.Leaderboard(
    database: Database
) {
    val context = LocalContext.current
    var mutableUserList by remember {
        mutableStateOf<List<User>?>(null)
    }

    LaunchedEffect(database) {
        database.getTopNUsers(10).thenDo { userRefList ->
            Tasks.whenAllSuccess<User>(userRefList.map { it.fetch() })
        }
            .thenMap {
                mutableUserList = it
                null
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Something went wrong when fetching users",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    FetchComponent(lazyRef = { loggedUserRef }) { currentUser ->
        if (mutableUserList != null) {
            Leaderboard(users = mutableUserList!!, currentUser = currentUser)
        }
    }
}

/**
 * Creates the leaderboard view.
 *
 * The top 3 users are given special styling (see [LeaderboardPodiumItem]).
 *
 * @param users The users of the leaderboard, ranked by score.
 * @param currentUser The current user viewing the leaderboard, as seen in the bottom of the screen.
 */
@Composable
fun Leaderboard(
    users: List<User>,
    currentUser: User
) {
    Column {
        // Wrap in a column to have minimal spacing with the chips
        Column(
            Modifier.padding(horizontal = 16.dp)
        ) {
            LeaderboardTitleBar()

            // Uncomment this line when/if different leaderboards are implemented
            // This is for a future user story...
            // LeaderboardChips()
        }

        // Note that the modifier argument can not be removed, as [Modifier.weight] is an extension
        // method only available in a [ColumnScope] or [RowScope]
        LeaderboardList(users = users, Modifier.weight(1.0f))

        // Bottom "You" item
        LeaderboardListItem(
            position = 953/* users.indexOf(currentUser)*/, // TODO: Hardcoded data!!
            user = currentUser,
            isCurrent = true
        )
    }
}