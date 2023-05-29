package com.github.geohunt.app.ui.screens.bounty_team_select

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.github.geohunt.app.model.Challenge
import com.github.geohunt.app.ui.components.utils.SkeletonLoadingImage

/**
 * This function creates a slider and returns the tuple (nb_elements, pager_state)
 * used to create a dot indicator.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun challengesImageSlider(challenges: List<Challenge>?, modifier: Modifier = Modifier): Pair<Int, PagerState>? {
    if (challenges.isNullOrEmpty()) return null

    val pagerState = rememberPagerState()

    BoxWithConstraints(modifier
        .clip(CardDefaults.shape)
        .height(250.dp)
    ) {
        HorizontalPager(pageCount = challenges.size, state = pagerState) {
            SkeletonLoadingImage(
                url = challenges[it].photoUrl,
                width = maxWidth,
                height = 250.dp,
                contentDescription = "Challenge $it image"
            )
        }
    }

    return Pair(challenges.size, pagerState)
}
