package com.github.geohunt.app

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ComposeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    //Hardcoded list used to test correct display of events on the map
    private var markerList: MutableList<MarkerOptions> = mutableListOf(MarkerOptions().position(LatLng(46.51958, 6.56398)).title("Event 1"), MarkerOptions().position(LatLng(46.52064, 6.56780)).title("Event 2"), MarkerOptions().position(LatLng(46.51881, 6.56779)).title("Event 3"))
    private val REQUEST_CODE_LOCATION_PERMISSION = 1
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map_container_view) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        addMarkersOnTheMap(googleMap, markerList)
        enableLocation()

        val epflLatitude = 46.519585
        val epflLongitude = 6.5684919
        val epflCoordinates = LatLng(epflLatitude, epflLongitude)

        val mapZoomValue = 15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(epflCoordinates, mapZoomValue))
    }

    private fun addMarkersOnTheMap(googleMap: GoogleMap, markerList : MutableList<MarkerOptions>){
        for (marker in markerList) {
            googleMap.addMarker(marker)
        }
    }

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

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }
}
