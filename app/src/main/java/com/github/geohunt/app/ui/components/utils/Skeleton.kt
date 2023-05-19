package com.github.geohunt.app.ui.components.utils

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

/**
 * Represents the grey skeleton box drawn when loading values.
 *
 * A blinking effect is used to have visual feedback to the user. This may cause the loading to be
 * perceived as faster, and hence improve the overall user experience.
 *
 * @param modifier The modifier applied to the skeleton box. This may be used to modify how the
 *                 skeleton is drawn on screen, e.g. width, height, border, etc.
 */
@Composable
fun Skeleton(modifier: Modifier = Modifier) {
    // Animate smoothly the box color to indicate loading
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = Color.Gray.copy(alpha = 0.4f),
        targetValue = Color.Gray.copy(alpha = 0.2f),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier.background(color).testTag("skeleton"))
}

/**
 * Displays a skeleton element while waiting for the value to be loaded.
 *
 * A grey [Skeleton] is displayed until the value is loaded (i.e is not null). Ideally, the box size
 * should be close to the real size of the content, so that there are no "content jumps", which is
 * poor user experience.
 *
 * The modifier argument can be used to customize how the box is rendered, such as the height, width,
 * and shape of the box. A good example of this principle is [SkeletonLoadingProfilePicture].
 *
 * When the value is loaded, a cross-fade animation is used to smoothly transition to the new content.
 *
 * @param value The element value, or null if there is none yet.
 * @param width The width of the grey skeleton box.
 * @param height The height of the grey skeleton box.
 * @param modifier The modifier applied to the skeleton element.
 * @param content The content that is drawn once the value is loaded.
 */
@Composable
fun <T> SkeletonLoading(
    value: T?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Crossfade(value) {
        when (it) {
            null -> Skeleton(modifier = modifier.size(width = width, height = height))
            else -> content(it)
        }
    }
}

/**
 * Represents a skeleton element for images.
 *
 * The skeleton is kept when the URL is finally provided, until the image is loaded by Coil's
 * [AsyncImage].
 *
 * The [width], [height] and [modifier] are applied on both the skeleton and the loaded image to
 * avoid visual jumps and provide the best loading experience.
 *
 * @param url The URL of the image, or null if it is not available yet.
 * @param width The width of the element.
 * @param height The height of the element.
 * @param modifier The modifier applied to both the skeleton and the image.
 * @param onClick An optional onClick handler.
 * @param contentDescription The image description for assistive technologies.
 */
@Composable
fun SkeletonLoadingImage(
    url: String?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentDescription: String
) {
    val sizeModifier = modifier.size(width = width, height = height)

    Crossfade(url) {
        when(it) {
            null -> { Skeleton(sizeModifier) }
            else -> {
                // Fixing the size here is important to avoid subcomposition
                // See https://coil-kt.github.io/coil/compose/#subcomposeasyncimage
                val request = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .size(
                        width = with(LocalDensity.current) { width.roundToPx() },
                        height = with(LocalDensity.current) { height.roundToPx() }
                    )
                    .crossfade(true)
                    .build()

                // Load the image and use the skeleton as long as the image is loading
                SubcomposeAsyncImage(
                    model = request,
                    contentDescription = contentDescription
                ) {
                    when(painter.state) {
                        is AsyncImagePainter.State.Loading,
                        is AsyncImagePainter.State.Error -> {
                            Skeleton(sizeModifier)
                        }

                        else -> {
                            val imageModifier = when(onClick) {
                                null -> sizeModifier
                                else -> sizeModifier.clickable(onClick = onClick)
                            }

                            SubcomposeAsyncImageContent(imageModifier, contentScale = ContentScale.Crop)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Creates a round skeleton placeholder for profile pictures.
 *
 * @param url The URL of the profile picture, or null if it not available yet.
 * @param size The size of the skeleton circle.
 * @param modifier The modifier to apply to both the skeleton and the image.
 * @param onClick An optional onClick handler.
 * @param contentDescription The image description for assistive technologies.
 *
 * @see [SkeletonLoadingImage]
 */
@Composable
fun SkeletonLoadingProfilePicture(
    url: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentDescription: String
) {
    return SkeletonLoadingImage(
        url = url,
        width = size,
        height = size,
        modifier = modifier.clip(CircleShape),
        onClick = onClick,
        contentDescription = contentDescription
    )
}