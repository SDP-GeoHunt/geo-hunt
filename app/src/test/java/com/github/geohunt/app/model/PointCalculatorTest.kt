package com.github.geohunt.app.model

import com.github.geohunt.app.model.database.api.Location
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for the PointCalculator
 * Note that these tests are somewhat empiric
 * and that changes to the hyper parameters of PointCalculator could break them
 * pretty easily. Writing tests depending on these hyper parameters would be too
 * complicated and not worth the time needed.
 */
class PointCalculatorTest {
    @Test
    fun calculatorGivesMaxPointsOnIdenticalLocation() {
        val leukerbad = Location(46.37810088109651, 7.625535288545297)

        assertEquals(PointCalculator.MAX_POINTS, PointCalculator.computePoints(leukerbad, leukerbad), 1e-3)
    }

    @Test
    fun calculatorGivesZeroPointsOnLongDistances() {
        //Distance of 2.75 km
        val lausanne = Location(46.51919259139398, 6.633433265031245)
        val pully = Location(46.50940191063235, 6.666380123092782)

        assertEquals(PointCalculator.MIN_POINTS, PointCalculator.computePoints(lausanne, pully), 1e-3)

        //Distance of 1.21 km
        val olympicMuseum = Location(46.50899750094015, 6.6338989782122315)
        assertEquals(PointCalculator.MIN_POINTS, PointCalculator.computePoints(lausanne, olympicMuseum), 1e-3)

        //Distance of 0.631 km
        //Delta has to be higher as function starts to increase around 600 meters with current hyper parameters
        val vigie = Location(46.52156105006732, 6.62389057575249)
        assertEquals(PointCalculator.MIN_POINTS, PointCalculator.computePoints(lausanne, vigie), 1e-2)
    }
}