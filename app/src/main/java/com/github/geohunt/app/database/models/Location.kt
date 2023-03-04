package com.github.geohunt.app.database.models

import java.nio.ByteBuffer
import java.util.zip.CRC32
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

data class Location(var latitude: Double,
                    var longitude: Double) {
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
