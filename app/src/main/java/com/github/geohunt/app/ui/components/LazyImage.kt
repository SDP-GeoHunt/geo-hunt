package com.github.geohunt.app.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.github.geohunt.app.R
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.ui.rememberLazyRef

@Composable
fun AsyncImage(
    contentDescription: String,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    factory: () -> LazyRef<Bitmap>
) {
    val lazyRef = rememberLazyRef(factory)

    if (lazyRef.value != null) {
        Image(
            painter = BitmapPainter(lazyRef.value!!.asImageBitmap()),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale
        )
    }
    else {
        Box(modifier = modifier.background(Color(R.color.md_theme_light_surface))) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
