package com.github.geohunt.app.utility

import java.time.Duration
import kotlin.math.roundToLong

/**
 * Quantize the current double value in discrete bins of size scale
 *
 * @throws UnsupportedOperationException if the rounding operation to long value cannot be safely
 * performed due to not enough bits in order to represent the double value
 */
fun Double.quantize(binSize: Double) : Double {
    // Argument sanitization
    require(binSize > 0.0) {
        "Double.quantize requires a non-negative bin-size but was provided $binSize"
    }

    return quantizeToLong(binSize).toDouble() * binSize
}

/**
 * Quantize the current double in bins and return the index of the specific bin, 0 being the bin
 * corresponding to all double value in [-1/2 * scale, 1/2 * scale]
 *
 * @throws UnsupportedOperationException if the index of the current bits cannot be represented by
 * a Long
 */
fun Double.quantizeToLong(binSize: Double) : Long {
    // Argument sanitization
    require(binSize > 0.0) {
        "Double.quantizeToLong requires a non-negative bin-size but was provided $binSize"
    }

    // Retrieve the value rescale so that a bin correspond to a unit
    val value = this / binSize

    // If this value is not a valid long return
    if (value > Long.MAX_VALUE.toDouble() || value < Long.MIN_VALUE.toDouble()) {
        throw UnsupportedOperationException("Cannot quantize the current value to Long because of overflows")
    }

    // Finally return the corresponding bin as Long
    return (this / binSize).roundToLong()
}
