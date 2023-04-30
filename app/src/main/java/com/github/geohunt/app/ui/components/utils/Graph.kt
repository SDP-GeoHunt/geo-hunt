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

@OptIn(ExperimentalTextApi::class)
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
    val textMeasurer = rememberTextMeasurer()
    Box(modifier = Modifier
            .border(BorderStroke(2.dp, Color.Black))
            .fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val xPadding = 125f
            val yPadding = 270f

            drawXLabels(this, textMeasurer, xStrings, xPadding, size.width, size.height - (yPadding/5))
            drawYLabels(this, textMeasurer, yStrings, yPadding, size.height, xPadding/5)
            val offsets = computeOffsets(
                    xValues, xBottom, xTop,
                    yValues, yBottom, yTop,
                    size.width, size.height, xPadding, yPadding)
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
        height: Float,
        xPadding: Float,
        yPadding: Float
): List<Offset> {
    val deltaX = xTop - xBottom
    val deltaXCanvas = width - 2*xPadding - 20f
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
            drawScope.drawText(textMeasurer, str, offset)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
fun drawYLabels(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        yStrings: List<String>,
        yPadding: Float,
        height: Float,
        xOffset: Float
) {
    //subtract 65 to take into account the "height" of the text
    val ySpacing = (height - yPadding - 45  ) / (yStrings.size - 1)
    for((i, str) in yStrings.reversed().withIndex()) {
        val offset = Offset(xOffset,ySpacing * i)
        drawScope.drawText(textMeasurer, str, offset)
    }
}