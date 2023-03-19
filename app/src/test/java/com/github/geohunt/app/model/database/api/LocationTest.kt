package com.github.geohunt.app.model.database.api

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The tests were written using the measure distance function of Google Maps
 */
class LocationTest {
    private val ERROR_RATIO = 0.3
    @Test
    fun distanceToWorksOnRandomPoints() {
        val l1 = Location(50.06638888888889, -5.714722222222222)
        val l2 = Location(58.64388888888889, -3.0700000000000003)

        val expected = 968.9
        assertEquals(expected, l1.distanceTo(l2), ERROR_RATIO * expected)

        val EPFLLausanne = Location(46.521390647018485, 6.564999687567261)
        val EPFLValais = Location(46.22699495676842, 7.3614545344484235)

        val expected2 = 69.06
        assertEquals(expected2, EPFLValais.distanceTo(EPFLLausanne), ERROR_RATIO * expected2)
    }

     @Test
     fun distanceToWorksOnClosePoints() {
         val museeBolo = Location(46.518743242394194, 6.563740532431146)
         val esplanade = Location(46.519997153650685, 6.565521838813867)

         val expected = 194.22e-3
         assertEquals(expected, museeBolo.distanceTo(esplanade), ERROR_RATIO * expected)
     }

    @Test
    fun distanceToWorksOnVeryClosePoints() {
        val cinemaPatheFlon = Location(46.52181215722814, 6.626885556273142)
        val MADCafeFlon = Location(46.52172583085868, 6.627082190671149)

        val expected = 19.13e-3
        assertEquals(expected, cinemaPatheFlon.distanceTo(MADCafeFlon), ERROR_RATIO * expected)
    }

    @Test
    fun testLocationDMS() {
        val location = Location(48.858283, 2.294645)
        MatcherAssert.assertThat(location.toString(), Matchers.anyOf(Matchers.equalTo("48° 51' 29.82\"N, 2° 17' 40.72\"E"),
        Matchers.equalTo("48° 51' 29,82\"N, 2° 17' 40,72\"E")))
    }

    @Test
    fun testLocationCoarseHash() {
        val location = Location(48.858283, 2.294645)
        MatcherAssert.assertThat(location.getCoarseHash(), Matchers.equalTo("163f921c"))
    }

    @Test
    fun testLocationCoarseLocation() {
        val location = Location(0.0, 0.0)
        location.latitude = 48.858283
        location.longitude = 2.294645
        MatcherAssert.assertThat(location.getCoarseLocation().latitude, Matchers.closeTo(48.9, 1e-5))
        MatcherAssert.assertThat(location.getCoarseLocation().longitude, Matchers.closeTo(2.3, 1e-5))
    }
}