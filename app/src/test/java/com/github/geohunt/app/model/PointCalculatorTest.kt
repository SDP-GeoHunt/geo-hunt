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

    @Test
    fun calculatorGivesAlmostMaxPointsOnShortDistances() {
        //Here we reuse the function that tests equality with a big delta in a somewhat unusual manner.
        //This is because we want the points to be within some general range of points

        //Distance of 4.6 m
        val museeBolo = Location(46.51871132171965, 6.563740024427009)
        val INF = Location(46.518751465143225, 6.563753435471584)

        assertEquals(PointCalculator.MAX_POINTS - 125, PointCalculator.computePoints(museeBolo, INF), 125.0)

        //Distance of 43 m
        val INM = Location(46.51862118400189, 6.563185765613928)
        assertEquals(PointCalculator.MAX_POINTS - 125, PointCalculator.computePoints(museeBolo, INM), 125.0)
    }

    @Test
    fun calculatorGivesHalfPointsOnMediumDistances() {
        //Distance of 152 m
        val museeBolo = Location(46.51871132171965, 6.563740024427009)
        val ornithorynque = Location(46.520090585931456, 6.5638286979646745)

        assertEquals(PointCalculator.MAX_POINTS / 2, PointCalculator.computePoints(museeBolo, ornithorynque), 500.0)

    }
}