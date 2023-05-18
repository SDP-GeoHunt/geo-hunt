package com.github.geohunt.app.model.points

import com.github.geohunt.app.model.Challenge

/**
 * Interface representing a Point calculator, it only has one method used to compute the points
 * of two attributed to two locations
 */
interface PointCalculator {

    /**
     * The only function of this interface, computes the points awarded to two locations
     * @param distance The distance to the "real" position
     * @return the amount of points
     */
    fun computePoints(distance: Double): Long

    companion object {

        /**
         * Default calculators
         */
        val defaultCalculators = mapOf(
            Challenge.Difficulty.EASY to GaussianPointCalculator(0.20),
            Challenge.Difficulty.MEDIUM to GaussianPointCalculator(0.15),
            Challenge.Difficulty.HARD to GaussianPointCalculator(0.10)
        ).withDefault { GaussianPointCalculator(0.10) }

        /**
         * The least amount of points possible.
         * Note that the function used can't be negative so negative MIN_POINTS value
         * will work the same way as MIN_POINTS = 0
         */
        const val MIN_POINTS = 0L

        /**
         * The maximum amount of points possible
         */
        const val MAX_POINTS = 5000L
    }
}
