package com.github.geohunt.app.maps.marker

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDateTime
import java.time.Month

//Hardcoded list used to test correct display of events on the map
private val mockBitmap: Bitmap = Bitmap.createBitmap(IntArray(120*120){ Color.CYAN}, 90, 90, Bitmap.Config.ARGB_8888)
private val mockChallengeDatabase : List<Marker> = listOf(
    Marker(LatLng(46.51958, 6.56398), "Event 1", "Expires on 1 May 2023 at 19:39", mockBitmap, LocalDateTime.of(2023, Month.MAY, 1, 19, 39, 12)),
    Marker(LatLng(46.52064, 6.56780), "Event 2", "Expires on 2 May 2023 at 12:24", mockBitmap, LocalDateTime.of(2023, Month.MAY, 2, 12, 24, 35)),
    Marker(LatLng(46.51881, 6.56779), "Event 3", "Expires on 3 May 2023 at 16:12", mockBitmap, LocalDateTime.of(2023, Month.MAY, 3, 16, 12, 12))
    //MarkerData("Event 1", mockBitmap, LatLng(46.51958, 6.56398), LocalDateTime.of(2023, Month.MAY, 1, 19, 39, 12)),
    //MarkerData("Event 2", mockBitmap, LatLng(46.52064, 6.56780), LocalDateTime.of(2023, Month.MAY, 2, 12, 24, 35)),
    //MarkerData("Event 3", mockBitmap, LatLng(46.51881, 6.56779), LocalDateTime.of(2023, Month.MAY, 3, 16, 12, 12))
)

/**
 * Adds the data from the database to the map as markers
 */
@Composable
fun DisplayMarkers() {
    mockChallengeDatabase.forEach { challenge ->
        MarkerInfoWindowContent(
            state = rememberMarkerState(position = challenge.position),
            title = challenge.title,
            snippet = challenge.expiryDate.toString(),
            icon = BitmapDescriptorFactory.defaultMarker(geoHuntRed.red)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //The image displayed at the top of the info window
                    Image(
                        painter = painterResource(id = R.drawable.radar_icon),
                        contentDescription = "Radar Icon",
                        modifier = Modifier
                            .size(90.dp)
                            .padding(top = 16.dp),
                        contentScale = ContentScale.Crop)

                    Spacer(modifier = Modifier.height(16.dp))

                    //The middle text containing the title of the challenge
                    Text(
                        text = challenge.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.displayMedium,
                    )

                    //The bottom text containing the expiry date of the challenge
                    Text(
                        text = challenge.expiryDate.toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
