package com.github.geohunt.app.maps.marker

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.geoHuntRed
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MarkerClustering(items: List<Marker>) {
    var selectedMarker: Marker? by remember { mutableStateOf(null) }

    //var mark : Marker = null
    val isMarkerClicked = remember { mutableStateOf(false) }

    Clustering(
        items = items,

        //The content of the info window
        onClusterItemClick = { marker ->
            selectedMarker = marker
            //   mark = marker
            isMarkerClicked.value = true

            Log.d("TAG", "onClusterItemClick: $marker")

            //marker.state.hideInfoWindow()
            marker.state.showInfoWindow() //MAYBE ???


            true

        },


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
    MarkerInfoWindowContent(
        state = selectedMarker?.state ?: MarkerState(),
        onClick = {
            Log.d("TAG", "YOOOOOO MAN Non-cluster marker clicked! $it")
            true
        },
        title = "Fun fact",
    ) { marker ->
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
                        .padding(10.dp)
                )

                //The title of the marker
                Text(
                    text = "Fun fact",
                    //text = marker.markerTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )

                //The snippet of the marker
                Text(
                    text = "This is a fun fact",
                    //text = marker.markerSnippet,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }








    /*if (isMarkerClicked.value) {
        selectedMarker?.let { marker ->
            MarkerInfoWindow(
                state = marker.state,
                onClick = {
                    Log.d("TAG", "YOOOOOO MAN Non-cluster marker clicked! $it")
                    true
                }
            ) { marker ->
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
                                .padding(10.dp)
                        )

                        //The title of the marker
                        Text(
                            text = "Fun fact",
                            //text = marker.markerTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )

                        //The snippet of the marker
                        Text(
                            text = "This is a fun fact",
                            //text = marker.markerSnippet,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }*/
}

/*    if (isMarkerClicked.value) {
        selectedMarker?.let { marker ->
            DisplayMarkerInformation(marker = marker)
        }
    }*/
    //selectedMarker?.let {marker -> DisplayMarkerInformation(marker = marker)}

    //MarkerInfoWindow(
     //   s
//   tate = rememberMarkerState(position = LatLng(46.51881+0.1, 6.56779 + 0.1)),
    //    onClick = {
    //        Log.d("TAG", "Non-cluster marker clicked! $it")
    //        true
    //    }
    //)

    //for (marker in items) {
    //        DisplayMarkerInformation(marker = marker)
    //}


////EXP
    /*MarkerInfoWindow(
        state = rememberMarkerState(position = LatLng(46.51881, 6.56779)),
        onClick = {
            Log.d("MarkerInfoWindow", "Clicked")
        true
                  },

        //state = rememberMarkerState(),
        //title = marker.title,
        //snippet = marker.expiryDate.toString(),
        icon = BitmapDescriptorFactory.defaultMarker(geoHuntRed.red)
    ){marker ->
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
                 //   text = marker.title,
                    text = "Challenge Title",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium,
                )

                //The bottom text containing the expiry date of the challenge
                Text(
                //    text = marker.expiryDate.toString(),
                    text = "Expiry Date",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }*/










    //////EXP



























//{
        //marker ->
        //if (marker == selectedMarker) {
        //}
//    }
    //selectedMarker?.let { DisplayMarkerInformation(marker = it) }

    ////MarkerInfoWindow(


    ///selectedMarker?.let {  BoxMarkerInformation(marker = it) }
    //if (isMarkerClicked.value) {
     //   DisplayMarkerInformation(marker = mark)
    //}
    //DisplayMarkerInformation(marker = mark)

    //{ marker ->
    //    DisplayMarkerInformation(marker = marker)
   // }

    //for (marker in items) {
    //    DisplayMarkerInformation(marker = marker)
    //}



    //MarkerInfoWindow(
    //    state = rememberMarkerState(position = LatLng(46.51881, 6.56779)),
    //    onClick = {
    //        true
    //    }
    //)




