package com.github.geohunt.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.github.geohunt.app.model.database.api.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.FetchComponent

/**
 * A component that allows to like a challenge
 */
@Composable
fun ChallengeLikeView(db : Database, user: User, challengeId: String, modifier: Modifier = Modifier) {
    IconButton(
        modifier = Modifier
            .size(40.dp)
            .padding(10.dp)
            .background(colorResource(id = R.color.md_theme_dark_background)),
        onClick = { db.insertUserLike(user.uid, challengeId)}
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = "Like challenge",
            tint = colorResource(id = R.color.md_theme_light_onBackground)
        )
    }
}
