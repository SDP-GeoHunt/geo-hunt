package com.github.geohunt.app.event.marker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.geohunt.app.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class EventMarkerActionDisplay(private val context: Context) : GoogleMap.InfoWindowAdapter {
    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker): View? {
        // Obtain the data from the marker's tag
        val data = marker.tag as Triple<*, *, *>

        // Inflate the view and set the data
        val view = LayoutInflater.from(context).inflate(R.layout.event_marker_field, null)

        // Load the image from the marker's tag
        view.findViewById<ImageView>(R.id.text_view_title).setImageBitmap(data.first as Bitmap?)

        // Load the challenge expiration date from the marker's tag
        view.findViewById<TextView>(R.id.text_expiration_date).text = data.third.toString()

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        // Return null to use the default info window frame
        return null
    }
}
