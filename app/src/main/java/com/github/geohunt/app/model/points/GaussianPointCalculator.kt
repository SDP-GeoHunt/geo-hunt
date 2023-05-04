package com.github.geohunt.app.model.points

import com.github.geohunt.app.model.points.PointCalculator.Companion.MAX_POINTS
import com.github.geohunt.app.model.points.PointCalculator.Companion.MIN_POINTS
import com.github.geohunt.app.utility.clamp
import com.github.geohunt.app.utility.gaussianDistributionPDF

/**
 * Implementation of a point calculator, computes points using a Gaussian function
 * the standard deviation is given as a parameter to constructor of the class
 * @param std Standard deviation used in the gaussian function
 */
class GaussianPointCalculator(std: Double) : PointCalculator {
    /**
     * Utility definitions
     */
    private val calculator = gaussianDistributionPDF(0.0, std)
    private val fctMaxValue = calculator(0.0)
    private val ratio = MAX_POINTS / fctMaxValue

    /**
     * Main function of the PointCalculator, computes the amount of points a claim is worth.
     * Uses a gaussian distribution and the distance between the two Locations to compute
     * the points.
     * @param distance The distance to the real location
     */
    override fun computePoints(distance: Double): Long {
        val points = calculator(distance) * ratio
        return clamp(MIN_POINTS, points.toLong(), MAX_POINTS)
    }
}