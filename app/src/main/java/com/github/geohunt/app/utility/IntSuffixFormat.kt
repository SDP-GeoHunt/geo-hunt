package com.github.geohunt.app.utility

/**
 * Returns a string representation of this integer value, with a suffix indicating the order of magnitude.
 * For example, if this value is 1500, the returned string will be "1.5k". If this value is 15000000,
 * the returned string will be "15M".
 *
 * @throws IllegalArgumentException if this value is negative.
 * @return A string representation of this integer value with a suffix, or the original string if it is less than 1000.
 */
fun Int.toSuffixedString() : String {
    require(this >= 0) { "Negative values are not allowed." }

    return when {
        this >= 10_000_000 -> "${this / 1_000_000}M"
        this >= 1_000_000 -> "${(this / 1e6).quantizeToString(0.1)}M"
        this >= 10_000 -> "${this / 1_000}k"
        this >= 1_000 -> "${(this / 1e3).quantizeToString(0.1)}k"
        else -> "$this"
    }
}
