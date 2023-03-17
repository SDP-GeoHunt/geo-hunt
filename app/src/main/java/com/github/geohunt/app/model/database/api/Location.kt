package com.github.geohunt.app.model.database.api

import com.github.geohunt.app.utility.quantize
import com.github.geohunt.app.utility.quantizeToLong
import com.google.firebase.database.Exclude
import java.nio.ByteBuffer
import java.util.zip.CRC32
import java.lang.Math.toRadians
import kotlin.math.*

/**
 * Represent the location on earth using the latitude and longitude as double valued numbers
 */
data class Location(var latitude: Double = 0.0,
                    var longitude: Double = 0.0) {
    /**
     * Computes the distance in kilometers to another Location
     * Uses the haversine formula to perform the calculation (https://en.wikipedia.org/wiki/Haversine_formula)
     * @param that Location to which we want to calculate the distance
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

    /**
     * Convert the current location to the degrees-minutes-seconds more standardized notation
     */
    override fun toString(): String {
        return "${getDMS(latitude, 'N', 'S')}, ${getDMS(longitude, 'E', 'W')}"
    }

    /**
     * Return an approximate location of the current location corresponding to the one obtained by
     * rounding the location latitude and longitude on the closest point on a ~11.1km lattice (at equator)
     */
    @Exclude
    fun getCoarseLocation(): Location {
        return Location(
            latitude.quantize(0.1),
            longitude.quantize(0.1)
        )
    }

    /**
     * Return the hash corresponding with the current coarse location.
     *
     * Notice that this should be preferred to getCoarseLocation().hash() as it does not account
     * rounding error and as such is more suited for retrieval method
     */
    @Exclude
    fun getCoarseHash() : String {
        val crc32 = CRC32()

        // Create the byte buffer
        crc32.update(
            ByteBuffer.allocate(2 * Long.SIZE_BYTES)
                .putLong(0, latitude.quantizeToLong(0.1))
                .putLong(Long.SIZE_BYTES, longitude.quantizeToLong(0.1))
        )

        // Finally return the result in form of a string
        return crc32.value.toString(16)
    }

    @Exclude
    private fun getDMS(v: Double, positive: Char, negative: Char) : String {
        var value = v.absoluteValue

        val degree = value.toInt()

        value = (value - degree) * 60.0
        val minutes = value.toInt()

        value = (value - minutes) * 60.0

        return "$degree° $minutes' ${String.format("%.2f", value)}\"${if (v > 0.0) positive else negative}"
    }

    companion object {
        /**
         * Mean earth radius in kilometers, used to compute the distance between two Locations
         */
        private const val EARTH_MEAN_RADIUS = 6371
        
         /**
         * Length of the string representing the coarse hash
         */
        const val COARSE_HASH_SIZE = (2 * Long.SIZE_BYTES) / 2

        fun getCoarseHash(location: Location) : String {
            val crc32 = CRC32()

            // Define a ~11.1km lattice (at the equator)
            val coarseLatitude = (location.latitude * 10.0).roundToLong()
            val coarseLongitude = (location.longitude * 10.0).roundToLong()

            val byteBuffer = ByteBuffer.allocate(2 * Long.SIZE_BYTES)
                    .putLong(0, coarseLatitude)
                    .putLong(Double.SIZE_BYTES, coarseLongitude)
            crc32.update(byteBuffer)
            return crc32.value.toString(36)
        }
    }
}


