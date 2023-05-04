package com.github.geohunt.app.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.ui.FetchComponent

/**
 * A composable function that asynchronously fetches and displays an image from a provided factory function.
 *
 * @param contentDescription The content description of the image for accessibility purposes.
 * @param modifier The modifier to be applied to the layout.
 * @param alignment The alignment of the image within its parent layout.
 * @param contentScale The scaling behavior of the image.
 * @param factory A factory function that returns a LazyRef object representing the image to be displayed.
 */
@Composable
fun AsyncImage(
    contentDescription: String,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    factory: () -> LazyRef<Bitmap>
) {
    Box(modifier = modifier) {
        FetchComponent(
            lazyRef = factory,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(painter = BitmapPainter(it.asImageBitmap()),
                contentDescription = contentDescription,
                modifier = modifier,
                alignment = alignment,
                contentScale = contentScale
            )
        }
    }
}
