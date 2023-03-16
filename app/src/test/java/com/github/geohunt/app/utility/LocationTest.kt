package com.github.geohunt.app.utility

import com.github.geohunt.app.model.database.api.Location
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class LocationTest {
    @Test
    fun testLocationDMS() {
        val location = Location(48.858283, 2.294645)
        assertThat(location.toString(), equalTo("48° 51' 29.82\"N, 2° 17' 40.72\"E"))
    }

    @Test
    fun testLocationCoarseHash() {
        val location = Location(48.858283, 2.294645)
        assertThat(location.getCoarseHash(), equalTo("163f921c"))
    }

    @Test
    fun testLocationCoarseLocation() {
        val location = Location(0.0, 0.0)
        location.latitude = 48.858283
        location.longitude = 2.294645
        assertThat(location.getCoarseLocation().latitude, closeTo(48.9, 1e-5))
        assertThat(location.getCoarseLocation().longitude, closeTo(2.3, 1e-5))
    }
}