package com.github.geohunt.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.geohunt.app.R

/**
 * A composable function that displays a zoomable image view with an option to go back to the
 * previous screen.
 *
 * @param url The ID of the image to be displayed.
 * @param fnGoBackCallback function called whenever the user presses on the go-back arrow
 */
@Composable
fun ZoomableImageView(url: String, fnGoBackCallback: () -> Unit) {
    Box(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.md_theme_light_background))) {
        ZoomableBox(modifier = Modifier.fillMaxSize()) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                    contentDescription = "Zoomable Image",
                    modifier = Modifier
                            .fillMaxSize()
                            .applyZoom()
                            .testTag("image-view-$url"),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center)
        }


        GoBackBtn(fnGoBackCallback)
    }
}
