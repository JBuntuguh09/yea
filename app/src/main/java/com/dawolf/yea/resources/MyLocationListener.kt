package com.dawolf.yea.resources

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class MyLocationListener(val context: Context) : LocationListener {

    var lat = "0"
    var long = "0"
    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        // Do something with latitude and longitude
        lat = latitude.toString()
        long = longitude.toString()

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle status changes if needed
    }

    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled if needed
    }

    override fun onProviderDisabled(provider: String) {
        // Handle provider disabled if needed
    }
}

//// Example usage in your activity or service
//val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//val locationListener = MyLocationListener(this)
//
//// Request location updates (you need to handle location permissions)
//locationManager.requestLocationUpdates(
//LocationManager.GPS_PROVIDER,
//0, // minTime in milliseconds
//0f, // minDistance in meters
//locationListener
//)

// To stop receiving updates when you no longer need them
// locationManager.removeUpdates(locationListener)
