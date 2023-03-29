package com.github.geohunt.app.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.model.BaseLazyRef
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge
import com.github.geohunt.app.model.database.api.Claim
import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.database.api.User
import com.github.geohunt.app.ui.FetchComponent
import com.github.geohunt.app.ui.components.challengeview.BellowImageButtons
import com.github.geohunt.app.ui.components.challengeview.ClaimCard
import com.github.geohunt.app.ui.components.challengeview.MainUserView
import com.github.geohunt.app.ui.components.user.ProfileIcon
import com.github.geohunt.app.ui.homescreen.HorizontalDivider
import com.github.geohunt.app.utility.DateFormatUtils.getElapsedTimeString
import com.github.geohunt.app.utility.toSuffixedString
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.time.LocalDateTime

private const val lorumIpsum =
    """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc bibendum enim non iaculis malesuada. Praesent non accumsan eros. Ut ut eros dolor. Interdum et malesuada fames ac ante ipsum primis in faucibus. Phasellus scelerisque eros nec porta facilisis. Mauris quis consectetur libero, nec vestibulum leo. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc vulputate ipsum eu felis consectetur mollis. Proin varius dui felis, sit amet mattis nibh sollicitudin et. Nunc semper felis tortor, quis tempus sapien porta eget. Proin convallis est nec mauris efficitur posuere. Nam sit amet varius tellus, et mollis enim. Sed vel ligula imperdiet, cursus arcu eget, consequat turpis. Suspendisse potenti.

Praesent bibendum non dolor eu fringilla. Etiam ac lorem sit amet quam auctor volutpat. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Fusce accumsan laoreet tellus, vel eleifend tortor venenatis eget. Suspendisse fermentum tellus eget vestibulum tincidunt. Donec sed tempus libero. Vestibulum pellentesque tempus sodales. Suspendisse eros risus, egestas nec porta et, pulvinar at lorem. Nulla a ante sed enim pretium vehicula ut ac eros. Nullam sollicitudin justo eu est sagittis, at vulputate mauris interdum. Sed non tellus interdum, placerat velit nec, pharetra magna."""



@Composable
fun ChallengeView(
    challenge: Challenge,
    onButtonBack: () -> Unit,
    displayImage: (String) -> Unit
) {
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }

    val lazyState = rememberLazyListState()
    val transition = updateTransition(
        remember { derivedStateOf { lazyState.firstVisibleItemIndex != 0 } },
        label = "Image size transition"
    )

    val imageAspectRatio by transition.animateFloat(label = "Animate image size ratio") { isScrolling ->
        if (isScrolling.value) 1.8f else 1.0f
    }

    Box {
        Column {
            // Main challenge image
            AsyncImage(
                contentDescription = "Challenge Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        displayImage(challenge.thumbnail.id)
                    }
                    .aspectRatio(imageAspectRatio, false),
                contentScale = ContentScale.Crop
            ) {
                challenge.thumbnail
            }

            BellowImageButtons(challenge)

            Spacer(Modifier.height(2.dp))

            HorizontalDivider(
                padding = 2.dp
            )

            LazyColumn(
                state = lazyState,
                modifier = Modifier.fillMaxHeight()
            ) {
                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                item {
                    MainUserView(challenge = challenge)
                    HorizontalDivider(padding = 2.dp)
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = if (isDescriptionExpanded) Int.MAX_VALUE.dp else 90.dp)
                            .padding(10.dp),
                        elevation = 10.dp
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                lorumIpsum,
                                fontSize = 11.sp,
                                color = MaterialTheme.colors.onBackground,
                                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                                modifier = Modifier.padding(10.dp),
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = if (isDescriptionExpanded) "less..." else "more...",
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 5.dp)
                                    .clickable {
                                        isDescriptionExpanded = !isDescriptionExpanded
                                    }
                            )
                        }
                    }
                }

                items(challenge.claims.size) { index: Int ->
                    ClaimCard(claimRef = challenge.claims[index], displayImage = displayImage)
                }
            }
        }

        // Go back button (for navigation)
        GoBackBtn(onButtonBack)
    }
}
