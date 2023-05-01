package com.github.geohunt.app.ui.components.utils

import androidx.compose.ui.geometry.Offset
import org.junit.Assert
import org.junit.Test

class GraphTest {
    @Test
    fun offsetsAreCorrectlyComputed() {
        val values = listOf(0L, 10, 25, 50, 60)
        val offsets = computeOffsets(values, 0, 100,
                                     values, 0, 100,
                                     1000f, 500f, 0f, 0f)

        val expected = listOf(Offset(0f, 500f), Offset(100f, 450f),
                Offset(250f, 375f), Offset(500f, 250f),
                Offset(600f, 200f))

        Assert.assertEquals(expected, offsets)
    }

    @Test
    fun labelsAreCorrectlyComputed() {
        val labels = findBestLabelSpacing(0, 1000, 1000f)
        val expected = (0L..10L).toList().map { it*100 }
        Assert.assertEquals(expected, labels)
    }
}