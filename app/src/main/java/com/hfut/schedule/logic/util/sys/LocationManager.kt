package com.hfut.schedule.logic.util.sys

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.xah.uicommon.util.LogUtil


@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getLocation(context : Context, onLocation : (com.hfut.schedule.logic.model.Location) -> Unit) {
    val PROVIDER = LocationManager.GPS_PROVIDER
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(PROVIDER, 1000L, 1f, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onLocation(com.hfut.schedule.logic.model.Location(location.longitude, location.latitude))
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })
    } catch (e: SecurityException) {
        LogUtil.error(e)
    }
}