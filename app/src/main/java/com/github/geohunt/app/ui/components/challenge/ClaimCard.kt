package com.github.geohunt.app.ui.components.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.i18n.toSuffixedString
import com.github.geohunt.app.model.Claim
import com.github.geohunt.app.ui.components.LabelledIcon
import com.github.geohunt.app.ui.components.utils.AwaitNullable

@Composable
fun ClaimCard(
    viewModel: ChallengeViewModel,
    claim: Claim,
    fnViewImageCallback: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.8f, false)
            .padding(5.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(claim.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center)
                        .clickable {
                            fnViewImageCallback(claim.photoUrl)
                        },
                    contentDescription = "Claimed image")
            }

            UserView(viewModel, claim)
        }
    }
}

@Composable
private fun UserView(
    viewModel: ChallengeViewModel,
    claim: Claim
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 2.dp)
            .height(39.dp)
    ) {
        AwaitNullable(state = remember { viewModel.retrieveUser(claim.id) }.collectAsState()) { author ->
            // Profile Icon
            //

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = author.name,
                    fontSize = 16.sp
                )

                Row {
                    Text(
                        text = DateFormatUtils.getElapsedTimeString(
                            claim.claimDate,
                            R.string.claimed_format
                        ) + " · ",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = MaterialTheme.colors.primary
                    )

                    LabelledIcon(
                        text = claim.distance.toSuffixedString() + "m",
                        painter = painterResource(id = R.drawable.ruler_measure),
                        contentDescription = "Distance",
                        fontColor = MaterialTheme.colors.primary,
                        fontSize = 11.sp,
                        iconSize = 11.dp
                    )
                }
            }
        }
    }
}

