package com.github.geohunt.app.ui.components.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.ui.theme.geoHuntRed
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Represents a graph, the points of the graph are given as arguments
 * Also draws labels using xStrings for the x axis and the best match for the y axis
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun Graph(
        xValues: List<Long>,
        xBottom: Long,
        xTop: Long,
        xStrings: List<String>,
        yValues: List<Long>
) {
    val textMeasurer = rememberTextMeasurer()
    Box(modifier = Modifier
            .border(BorderStroke(2.dp, Color.Black))
            .fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val xPadding = 125f
            val yPadding = 270f
            val yGraphSize = size.height - yPadding - 45

            val yLabelValues = findBestLabelSpacing(yValues.min(), yValues.max(), yGraphSize)
            drawXLabels(this, textMeasurer, xStrings, xPadding, size.width, size.height - (yPadding/5))
            drawYLabels(this, textMeasurer, yLabelValues.map { it.toString() }, yGraphSize, xPadding/5)
            val offsets = computeOffsets(
                    xValues, xBottom, xTop,
                    yValues, yLabelValues.first(), yLabelValues.last(),
                    size.width, size.height, xPadding, yPadding)
            drawPoints(offsets, PointMode.Polygon, color = geoHuntRed, strokeWidth = 4f)
        }
    }
}

/**
 * Find the best spacing based on the range of points we want to represent and the size of the window
 * Chooses the spacings along the list possibleSpacing defined below
 */
fun findBestLabelSpacing(min: Long, max: Long, size: Float): List<Long> {
    val possibleSpacings = listOf(50, 100, 500, 1000, 5000, 10000)
    val delta = (max - min).toFloat()
    //take best spacing s.t. it gives some minimal distance between points on the screen
    val bestSpacing = possibleSpacings.firstOrNull { (it / delta) * size >= 75f }
            ?: possibleSpacings.last()

    val bottomX = floor(min.toFloat() / bestSpacing).toLong()
    val topX = ceil(max.toFloat() / bestSpacing).toLong()
    val range = bottomX..topX
    return range.toList().map { it * bestSpacing }
}

/**
 * Computes the offsets of points (x, y)
 * Does this by applying a transformation from the real values to canvas values
 */
fun computeOffsets(
        x: List<Long>,
        xBottom: Long,
        xTop: Long,
        y: List<Long>,
        yBottom: Long,
        yTop: Long,
        width: Float,
        height: Float,
        xPadding: Float,
        yPadding: Float
): List<Offset> {
    val deltaX = xTop - xBottom
    val deltaXCanvas = width - 2*xPadding
    val offsetX = x.map { deltaXCanvas * ((it - xBottom).toFloat() / deltaX) }

    val deltaY = yTop - yBottom
    val deltaYCanvas = (height - yPadding)
    //Note that the origin of the canvas is on the top left and our system's origin on the bottom left
    val offsetY = y.map { height - deltaYCanvas * ((it - yBottom).toFloat() / deltaY) }

    return offsetX.zip(offsetY).map { Offset(xPadding + it.first, -yPadding + it.second) }
}

@OptIn(ExperimentalTextApi::class)
private fun drawXLabels(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        strings: List<String>,
        xPadding: Float,
        width: Float,
        yOffset: Float
) {
    //Subtract 70 to take "height" and rotation of text into account
    val xSpacing = (width - xPadding*2 - 70) / (strings.size - 1)
    val rotationAngle = 275f
    for ((i, str) in strings.withIndex()) {
        val offset = Offset(xPadding + xSpacing * i, yOffset)
        drawScope.rotate(rotationAngle, offset) {
            drawText(textMeasurer, str, offset)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun drawYLabels(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        yStrings: List<String>,
        height: Float,
        xOffset: Float
) {
    //subtract 65 to take into account the "height" of the text
    val ySpacing = height / (yStrings.size - 1)
    for((i, str) in yStrings.reversed().withIndex()) {
        val offset = Offset(xOffset,ySpacing * i)
        drawScope.drawText(textMeasurer, str, offset)
    }
}