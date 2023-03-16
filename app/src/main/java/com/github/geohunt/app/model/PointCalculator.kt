package com.github.geohunt.app.model

import com.github.geohunt.app.model.database.api.Location
import kotlin.math.*

/**
 * Object giving access to its only method computePoints, which allows the user to compute
 * the amount of points a claim gets.
 */
object PointCalculator {
    /**
     * Hyper parameters of the function used to compute the points
     */
    private const val MEAN = 0.0
    private const val STD = 0.15

    /**
     * The least amount of points possible.
     * Note that the function used can't be negative so negative MIN_POINTS value
     * will work the same way as MIN_POINTS = 0
     */
    const val MIN_POINTS = 0.0

    /**
     * The maximum amount of points possible
     */
    const val MAX_POINTS = 5000.0

    /**
     * Utility definitions
     */
    private val calculator = gaussianDistributionPDF(MEAN, STD)
    private val fctMaxValue = calculator(MEAN)
    private val ratio = MAX_POINTS / fctMaxValue

    /**
     * Main function of the PointCalculator, computes the amount of points a claim is worth.
     * Uses a gaussian distribution and the distance between the two Locations to compute
     * the points.
     * @param l1 First location used to compute points
     * @param l2 Second location used to compute points
     */
    fun computePoints(l1: Location, l2: Location): Double {
        val points = calculator(l1.distanceTo(l2)) * ratio
        return clamp(MIN_POINTS, points, MAX_POINTS)
    }

    //TODO Should maybe be moved to some MathUtils class along with clamp
    /**
     * Returns a function representing the PDF of the gaussian distribution with given mean
     * and standard deviation (std)
     * @param mean Mean of the gaussian
     * @param std Standard deviation of the gaussian
     */
    private fun gaussianDistributionPDF (mean: Double, std: Double): (Double) -> Double {
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
    private fun clamp(min: Double, x: Double, max: Double): Double {
        return max(min(x, max), min)
    }

}