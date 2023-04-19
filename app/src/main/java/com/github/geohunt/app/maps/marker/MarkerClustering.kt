package com.github.geohunt.app.maps.marker

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MarkerClustering(items: List<Marker>) {
    Clustering(
        items = items,

        clusterContent = { cluster ->
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RectangleShape,
                color = geoHuntRed,
                contentColor = Color.Blue,
                border = BorderStroke(2.dp, Color.Green)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "%,d".format(cluster.size),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,

                    )
                }
            }

        }

    )
    MarkerInfoWindow(
        state = rememberMarkerState(position = LatLng(46.51881, 6.56779)),
        onClick = {
            true
        }
    )

    ///REMOVE LATER
    /*MarkerInfoWindowContent(
        // state = rememberMarkerState(position = marker.position),
        //title = marker.title,
        //snippet = marker.expiryDate.toString(),
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
                    text = "test",
                    //  text = marker.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium,
                )

                //The bottom text containing the expiry date of the challenge
                Text(
                    text = "test",
                    // text = marker.expiryDate.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }*/

}
