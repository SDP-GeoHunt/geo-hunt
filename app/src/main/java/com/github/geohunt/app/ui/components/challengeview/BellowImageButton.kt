package com.github.geohunt.app.ui.components.challengeview

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
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.ui.components.LabelledIcon

@Composable
internal fun BellowImageButtons(challenge: Challenge) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(15.dp, 5.dp)
    ) {
        val fontSize = 18.sp
        val iconSize = 22.dp

        LabelledIcon(
            text = "42",
            painter = painterResource(R.drawable.likes),
            contentDescription = "Likes",
            fontSize = fontSize,
            iconSize = iconSize
        )

        Spacer(
            modifier = Modifier
                .width(18.dp)
                .weight(0.2f)
        )

        LabelledIcon(
            text = challenge.claims.size.toString(),
            painter = painterResource(R.drawable.target_arrow),
            contentDescription = "Claims",
            fontSize = fontSize,
            iconSize = iconSize
        )

        Spacer(
            modifier = Modifier
                .width(18.dp)
                .weight(0.2f)
        )

        LabelledIcon(
            text = "+25",
            painter = painterResource(R.drawable.cards_diamond),
            tint = Color(R.color.md_theme_light_tertiary),
            contentDescription = "Points",
            fontSize = fontSize,
            iconSize = iconSize
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .size(80.dp, 28.dp)
                .align(Alignment.CenterVertically),
            contentPadding = PaddingValues(2.dp, 2.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = { /*TODO*/ })
        {
            Text(
                text = "Join",
                fontSize = 17.sp
            )
        }
    }
}
