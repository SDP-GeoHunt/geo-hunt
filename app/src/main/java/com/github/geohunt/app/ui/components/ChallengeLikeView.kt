package com.github.geohunt.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.model.database.api.User
import com.google.android.gms.tasks.Task

/**
 * A component that allows to like a challenge
 */
@Composable
fun ChallengeLikeView(db : Database, user: User, challengeId: String, modifier: Modifier = Modifier) {
    var isLiked: Task<Boolean> = db.isUserLiked(user.uid, challengeId)
    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        Button(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(40.dp)
                .padding(10.dp)
                .background(colorResource(id = R.color.md_theme_dark_background)),
            onClick = {
                db.insertUserLike(user.uid, challengeId)

                // Update the like status
                isLiked = db.isUserLiked(user.uid, challengeId)

                // Debug
                if (isLiked.isSuccessful){
                    print("Nice")
                    print(isLiked.result)
                }
            }
        ) {
            if (isLiked.isSuccessful) {
                if (isLiked.result) {
                    println("LIKED")

                    Icon(
                        painter = painterResource(id = R.drawable.challenge_like_star),
                        null
                    )
                } else {
                    println("Not liked")

                    Icon(
                        painter = painterResource(id = R.drawable.challenge_not_like_star),
                        null
                    )
                }
            }
        }
    }
}
