package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.LoggedUserContext
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.ui.rememberLazyRef

@Composable
internal fun LoggedUserContext.BellowImageButtons(challenge: Challenge) {
    val doesUserFollowThisChallenge = rememberLazyRef(null, { loggedUserRef }) { user ->
        user.activeHunts.any { it.id == challenge.cid }
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp, 5.dp)) {
        val fontSize = 18.sp
        val iconSize = 22.dp

        LabelledIcon(
            text = challenge.numberOfActiveHunters.toSuffixedString(),
            painter = painterResource(R.drawable.likes),
            contentDescription = "Number of active hunters", fontSize = fontSize, iconSize = iconSize)

        Spacer(modifier = Modifier
            .width(18.dp)
            .weight(0.2f))

        LabelledIcon(
            text = challenge.claims.size.toString(),
            painter = painterResource(R.drawable.target_arrow),
            contentDescription = "Claims", fontSize = fontSize, iconSize = iconSize)

        Spacer(modifier = Modifier
            .width(18.dp)
            .weight(0.2f))

        LabelledIcon(
            text = "+25",
            painter = painterResource(R.drawable.cards_diamond),
            tint = Color(R.color.md_theme_light_tertiary),
            contentDescription = "Scores", fontSize = fontSize, iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .size(80.dp, 28.dp)
                .align(Alignment.CenterVertically),
            contentPadding = PaddingValues(2.dp, 2.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = doesUserFollowThisChallenge != null,
            onClick = {
                if (doesUserFollowThisChallenge!!) {
                    challenge.leaveHunt()
                }
                else {
                    challenge.joinHunt()
                }
            })
        {
            Text(
                text = if (doesUserFollowThisChallenge == null) "???" else if(doesUserFollowThisChallenge) "Leave" else "Join",
                fontSize = 17.sp
            )
        }
    }
}
