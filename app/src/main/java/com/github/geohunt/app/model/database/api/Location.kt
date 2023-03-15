package com.github.geohunt.app.model.database.api

import java.nio.ByteBuffer
import java.util.zip.CRC32
import java.lang.Math.toRadians
import kotlin.math.*

data class Location(var latitude: Double,
                    var longitude: Double) {
    /**
     * Computes the distance in meters to another Location
     * Uses the haversine formula to perform the calculation (https://en.wikipedia.org/wiki/Haversine_formula)
     * @param that Location to which calculate the distance
     */
    fun distanceTo(that: Location): Double {
        val lat1 = toRadians(this.latitude)
        val lat2 = toRadians(that.latitude)
        val dLat = toRadians(that.latitude - this.latitude)
        val dLon = toRadians(that.longitude - this.longitude)

        val a = sin(dLat / 2) * sin(dLat/2) +
                (cos(lat1) * cos(lat2) * sin(dLon/2) * sin(dLon/2))

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return c * EARTH_MEAN_RADIUS
    }

    override fun toString(): String {
        return "${getDMS(latitude, 'N', 'S')}, ${getDMS(longitude, 'E', 'W')}"
    }

    private fun getDMS(v: Double, positive: Char, negative: Char) : String {
        var value = v.absoluteValue

        val degree = value.toInt()

        value = (value - degree) * 60.0
        val minutes = value.toInt()

        value = (value - minutes) * 60.0

        return "$degree° $minutes' ${String.format("%.2f", value)}'' ${if (v > 0.0) positive else negative}"
    }

    companion object {
        /**
         * Mean earth radius in meters, used to compute the distance between two Locations
         */
        private const val EARTH_MEAN_RADIUS = 6371e3

        fun getCoarseHash(location: Location) : String {
            val crc32 = CRC32()

            // Define a ~11.1km lattice (at the equator)
            var coarseLatitude = (location.latitude * 10.0).roundToLong()
            var coarseLongitude = (location.longitude * 10.0).roundToLong()

            val byteBuffer = ByteBuffer.allocate(2 * Long.SIZE_BYTES)
                    .putLong(0, coarseLatitude)
                    .putLong(Double.SIZE_BYTES, coarseLongitude)
            crc32.update(byteBuffer)
            return crc32.value.toString(36)
        }
    }
}
