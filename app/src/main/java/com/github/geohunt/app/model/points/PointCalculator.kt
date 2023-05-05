package com.github.geohunt.app.model.points

import com.github.geohunt.app.model.Challenge.Difficulty

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
         * Converts a challenge difficulty to a predetermined PointCalculator
         * @param difficulty the challenge difficulty to convert
         */
        fun fromDifficulty(difficulty: Difficulty): PointCalculator {
            return when (difficulty) {
                Difficulty.EASY -> GaussianPointCalculator(0.20)
                Difficulty.MEDIUM -> GaussianPointCalculator(0.15)
                Difficulty.HARD -> GaussianPointCalculator(0.10)
            }
        }

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
