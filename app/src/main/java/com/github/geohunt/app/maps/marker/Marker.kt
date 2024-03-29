package com.github.geohunt.app.maps.marker

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

/**
 * Data class representing a marker that represents
 * a challenge on the map
 *
 * @param id the title of the marker
 * @param image the image of the given challenge
 * @param coordinates the coordinates of the marker on the map
 * @param expirationDate the date at which the challenge expires
 */
data class Marker(
    val id: String,
    val image: String,
    val coordinates: LatLng,
    val expirationDate: LocalDateTime,
    )
