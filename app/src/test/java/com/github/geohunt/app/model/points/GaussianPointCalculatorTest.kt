package com.github.geohunt.app.model.points

import com.github.geohunt.app.model.database.api.Location
import com.github.geohunt.app.model.points.PointCalculator.Companion.MAX_POINTS
import com.github.geohunt.app.model.points.PointCalculator.Companion.MIN_POINTS
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for the PointCalculator
 * Note that these tests are somewhat empiric
 * and that changes to the hyper parameters of PointCalculator could break them
 * pretty easily. Writing tests depending on these hyper parameters would be too
 * complicated and not worth the time needed.
 */
class GaussianPointCalculatorTest {
    private val pointCalculator = GaussianPointCalculator(0.15)
    @Test
    fun calculatorGivesMaxPointsOnIdenticalLocation() {
        assertEquals(MAX_POINTS, pointCalculator.computePoints(0.0))
    }

    @Test
    fun calculatorGivesMinPointsOnFarLocation() {
        assertEquals(MIN_POINTS, pointCalculator.computePoints(100.0))
    }

    @Test
    fun calculatorGivesZeroPointsOnLongDistances() {
        //Distance of 2.75 km
        val lausanne = Location(46.51919259139398, 6.633433265031245)
        val pully = Location(46.50940191063235, 6.666380123092782)

        assertEquals(pointCalculator.computePoints(2.75), MIN_POINTS)

        //Distance of 1.21 km
        val olympicMuseum = Location(46.50899750094015, 6.6338989782122315)
        assertEquals(pointCalculator.computePoints(1.21), MIN_POINTS)

        //Distance of 0.631 km
        //Delta has to be higher as function starts to increase around 600 meters with current hyper parameters
        val vigie = Location(46.52156105006732, 6.62389057575249)
        assertEquals(pointCalculator.computePoints(0.631), MIN_POINTS)
    }

    @Test
    fun calculatorGivesAlmostMaxPointsOnShortDistances() {
        //Here we reuse the function that tests equality with a big delta in a somewhat unusual manner.
        //This is because we want the points to be within some general range of points

        //Distance of 4.6 m
        assertEquals(MAX_POINTS - 230, pointCalculator.computePoints(0.046))

        //Distance of 0.43 m
        assertEquals(MAX_POINTS - 202, pointCalculator.computePoints(0.043))
    }

    @Test
    fun calculatorGivesHalfPointsOnMediumDistances() {
        //Distance of 152 m
        assertEquals(MAX_POINTS / 2, pointCalculator.computePoints(0.1766))

    }
}