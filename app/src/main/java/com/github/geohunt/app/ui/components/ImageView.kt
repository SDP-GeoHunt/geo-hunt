package com.github.geohunt.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import com.github.geohunt.app.R
import com.github.geohunt.app.model.database.api.Database
import com.github.geohunt.app.ui.FetchComponent

/**
 * A composable function that displays a zoomable image view with an option to go back to the
 * previous screen.
 *
 * @param database The database containing the image to be displayed.
 * @param iid The ID of the image to be displayed.
 * @param fnGoBackCallback function called whenever the user presses on the go-back arrow
 */
@Composable
fun ZoomableImageView(database: Database, iid: String, fnGoBackCallback: () -> Unit)
{
    val image = database.getImageById(iid)

    Box(modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.md_theme_light_background))) {
        FetchComponent(lazyRef = { image }, modifier = Modifier.align(Alignment.Center)) { bitmap ->
            ZoomableBox(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier.fillMaxSize()
                        .applyZoom()
                        .testTag("image-view-$iid"),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = "Zoomable image")
            }
        }

        GoBackBtn(fnGoBackCallback)
    }
}
