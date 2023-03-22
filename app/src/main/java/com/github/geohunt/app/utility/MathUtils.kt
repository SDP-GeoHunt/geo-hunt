package com.github.geohunt.app.utility

import kotlin.math.*

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
 * Quantize the current double value in discrete bins then format it to string,
 * notice that this method ensures that the string generated is not polluted by
 * artifact due to string approximation
 *
 * @throws UnsupportedOperationException if the rounding operation to long value cannot be safely
 * performed due to not enough bits in order to represent the double value
 */
fun Double.quantizeToString(binSize: Double) : String {
    val decimalPlace = (log10(1.0 / binSize) + 0.49).roundToLong()
    return "%.${decimalPlace}f".format(this.quantize(binSize))
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

/**
 * Returns a function representing the PDF of the gaussian distribution with given mean
 * and standard deviation (std)
 * @param mean Mean of the gaussian
 * @param std Standard deviation of the gaussian
 */
fun gaussianDistributionPDF (mean: Double, std: Double): (Double) -> Double {
    return fun(x: Double): Double {
        val piFactor = sqrt(2 * PI)
        val exponent = (-1.0/2.0) * ((x - mean) / std) * ((x - mean) / std)
        return (1 / (std * piFactor)) * exp(exponent)
    }
}

/**
 * Restricts x between min and max
 * @param min the minimum value
 * @param x the value to restrict
 * @param max the maximum value
 */
fun clamp(min: Double, x: Double, max: Double): Double {
    return max(min(x, max), min)
}
