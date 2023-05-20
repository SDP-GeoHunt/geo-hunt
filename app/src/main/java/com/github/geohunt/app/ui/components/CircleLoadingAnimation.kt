package com.github.geohunt.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleLoadingAnimation(
    modifier: Modifier = Modifier,
    circleColor: Color = MaterialTheme.colors.primary,
    animationDelay : Int = 1000,
    size : Dp? = null
) {
    var circleScale by remember { mutableStateOf(0f) }

    val circleScaleAnimate = animateFloatAsState(
        targetValue = circleScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDelay)
        )
    )

    // This is called when the app is launched
    LaunchedEffect(Unit) {
        circleScale = 1f
    }

    // Animating the circle
    Box(
        modifier = modifier
            .size(size = size ?: 64.dp)
            .scale(scale = circleScaleAnimate.value)
            .border(
                width = (size ?: 64.dp).run { this / 16 },
                color = circleColor.copy(alpha = 1 - circleScaleAnimate.value),
                shape = CircleShape
            )
    ) {

    }
}