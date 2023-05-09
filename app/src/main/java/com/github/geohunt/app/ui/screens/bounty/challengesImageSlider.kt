package com.github.geohunt.app.ui.screens.bounty

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.github.geohunt.app.model.Challenge

/**
 * This function creates a slider and returns the tuple (nb_elements, pager_state)
 * used to create a dot indicator.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun challengesImageSlider(challenges: List<Challenge>?, modifier: Modifier = Modifier): Pair<Int, PagerState>? {
    if (challenges == null) return null
    if (challenges.isEmpty()) return null

    // The aspect ratio is given by the first
    val firstImagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(challenges[0].photoUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val pagerState = rememberPagerState()

    Column(modifier) {
        HorizontalPager(pageCount = challenges.size, state = pagerState) {
            val dimensionModifier = (firstImagePainter.state as? AsyncImagePainter.State.Success)
                ?.painter?.intrinsicSize?.let { size ->
                    Modifier
                        .aspectRatio(size.width / size.height)
                } ?: Modifier


            if (it == 0) {
                Image(
                    painter = firstImagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else
                AsyncImage(
                    challenges[it].photoUrl,
                    contentDescription = null,
                    modifier = dimensionModifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
        }
    }

    return Pair(challenges.size, pagerState)
}
