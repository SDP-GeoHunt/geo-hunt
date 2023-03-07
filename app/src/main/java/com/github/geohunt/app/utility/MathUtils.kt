package com.github.geohunt.app.utility

import kotlin.math.roundToLong

/**
 * Quantize the current double value in discrete bins of size 1/scale
 *
 * @throws UnsupportedOperationException if the rounding operation to long value cannot be safely
 * performed due to not enough bits in order to represent the double value
 */
fun Double.quantize(inverseBinSize: Double) : Double {
    return quantizeToLong(inverseBinSize).toDouble() / inverseBinSize
}

/**
 * Quantize the current double in bins and return the index of the specific bin, 0 being the bin
 * corresponding to all double value in [-1/(2*scale), 1/(2*scale)]
 *
 * @throws UnsupportedOperationException if the index of the current bits cannot be represented by
 * a Long
 */
fun Double.quantizeToLong(inverseBinSize: Double) : Long {
    val value = this * inverseBinSize

    if (value > Long.MAX_VALUE.toDouble() || value < Long.MIN_VALUE.toDouble()) {
        throw UnsupportedOperationException("Cannot quantize the current value to Long because of overflows")
    }

    return (this * inverseBinSize).roundToLong()
}
