package com.github.geohunt.app.ui.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

/**
 * A composable function that provides a zoomable box with a customizable scale range.
 *
 * @param modifier The modifier to be applied to this composable.
 * @param minScale The minimum scale allowed for the box. Default is 1.0f.
 * @param maxScale The maximum scale allowed for the box. Default is 10.0f.
 * @param content The composable content of the zoomable box.
 */
@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1.0f,
    maxScale: Float = 10.0f,
    content: @Composable ZoomableBoxScope.() -> Unit
)
{
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier
        .clip(RectangleShape)
        .onSizeChanged { size = it }
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale = maxOf(minScale, minOf(maxScale, scale * zoom))

                val maxX = (size.width * (scale - 1)) / 2
                val minX = -maxX
                val maxY = (size.height * (scale - 1)) / 2
                val minY = -maxY
                offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
            }
        }
    ) {
        val scope = ZoomableBoxScope(scale, offsetX, offsetY)
        scope.content()
    }
}

class ZoomableBoxScope(
    private val scale: Float,
    private val offsetX: Float,
    private val offsetY: Float
) {
    fun Modifier.applyZoom() : Modifier {
        return graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offsetX
            translationY = offsetY
        }
    }
}
