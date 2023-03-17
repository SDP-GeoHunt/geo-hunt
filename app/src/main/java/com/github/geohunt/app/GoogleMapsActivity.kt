package com.github.geohunt.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.geohunt.app.event.marker.EventMarkerActionDisplay
import com.github.geohunt.app.event.marker.MarkerData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDateTime
import java.time.Month

class GoogleMapsActivity() : AppCompatActivity(), OnMapReadyCallback {
    lateinit var map: GoogleMap
    private val REQUEST_CODE_LOCATION_PERMISSION = 1

    //Hardcoded list used to test correct display of events on the map
    private val mockBitmap: Bitmap = Bitmap.createBitmap(IntArray(120*120){ Color.CYAN}, 90, 90, Bitmap.Config.ARGB_8888)
    private val mockChallengeDatabase : List<MarkerData> = listOf(
            MarkerData("Event 1", mockBitmap, LatLng(46.51958, 6.56398), LocalDateTime.of(2023, Month.MAY, 1, 19, 39, 12)),
            MarkerData("Event 2", mockBitmap, LatLng(46.52064, 6.56780), LocalDateTime.of(2023, Month.MAY, 2, 12, 24, 35)),
            MarkerData("Event 3", mockBitmap, LatLng(46.51881, 6.56779), LocalDateTime.of(2023, Month.MAY, 3, 16, 12, 12)))

    /**
     * Called when the activity is starting
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map_container_view) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        mapFragment?.getMapAsync{ map ->
            map.setInfoWindowAdapter(EventMarkerActionDisplay(this))
        }
    }

    /**
     * Called when the map is ready to be used
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()

        val epflLatitude = 46.519585
        val epflLongitude = 6.5684919
        val epflCoordinates = LatLng(epflLatitude, epflLongitude)

        val mapZoomValue = 15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(epflCoordinates, mapZoomValue))

        addMarkersOnTheMap(googleMap, mockChallengeDatabase)
    }

    /**
     * Enables the location layer if the permission has been granted
     * Otherwise, requests the permission
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation()
            }
        }
    }

    /**
     * Checks if the user has granted the location permission
     * @return true if the permission is granted, false otherwise
     */
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Enables the location layer if the permission has been granted
     * Otherwise, requests the permission
     */
    private fun enableLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    /**
     * Adds the data from the database to the map as markers
     */
    fun addMarkersOnTheMap(map: GoogleMap, challenges: List<MarkerData>){
        challenges.forEach{challenge ->
            val marker = map.addMarker(
                    MarkerOptions()
                            .title(challenge.title)
                            .position(challenge.coordinates)
            )
            marker?.tag = challenge
        }
    }
}
