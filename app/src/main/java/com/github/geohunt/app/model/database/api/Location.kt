package com.github.geohunt.app.model.database.api

import com.github.geohunt.app.utility.quantize
import com.github.geohunt.app.utility.quantizeToLong
import com.google.firebase.database.Exclude
import java.nio.ByteBuffer
import java.util.zip.CRC32
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

/**
 * Represent the location on earth using the latitude and longitude as double valued numbers
 */
data class Location(var latitude: Double,
                    var longitude: Double) {
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
            latitude.quantize(10.0),
            longitude.quantize(10.0)
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
                .putLong(0, latitude.quantizeToLong(10.0))
                .putLong(Long.SIZE_BYTES, longitude.quantizeToLong(10.0))
        )

        // Finally return the result in form of a string
        return crc32.value.toString(16)
    }

    companion object {
        /**
         * Length of the string representing the coarse hash
         */
        const val COARSE_HASH_SIZE = (2 * Long.SIZE_BYTES) / 4;
    }

    @Exclude
    private fun getDMS(v: Double, positive: Char, negative: Char) : String {
        var value = v.absoluteValue

        val degree = value.toInt()

        value = (value - degree) * 60.0
        val minutes = value.toInt()

        value = (value - minutes) * 60.0

        return "$degree° $minutes' ${String.format("%.2f", value)}'' ${if (v > 0.0) positive else negative}"
    }

}


