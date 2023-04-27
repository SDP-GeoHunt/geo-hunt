package com.github.geohunt.app.ui.components.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.dp

@Composable
fun Graph(
        xValues: List<Long>,
        xBottom: Long,
        xTop: Long,
        xStrings: List<String>,
        yValues: List<Long>,
        yBottom: Long,
        yTop: Long,
        yStrings: List<String>
) {
    Box(modifier = Modifier.padding(10.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val offsets = computeOffsets(xValues, xBottom, xTop,
                    yValues, yBottom, yTop,
                    size.width, size.height)
            drawPoints(offsets, PointMode.Polygon, color = Color.Black, strokeWidth = 4f)
        }
    }
}

private fun computeOffsets(
        x: List<Long>,
        xBottom: Long,
        xTop: Long,
        y: List<Long>,
        yBottom: Long,
        yTop: Long,
        width: Float,
        height: Float
): List<Offset> {
    val deltaX = xTop - xBottom
    val offsetX = x.map { width * ((it - xTop).toFloat() / deltaX) }

    val deltaY = yTop - yBottom
    //Note that the origin of the canvas is on the top left and our system's origin on the bottom left
    val offsetY = y.map { height - height * ((it - yBottom).toFloat() / deltaY) }

    return offsetX.zip(offsetY).map { Offset(it.first, it.second) }
}