package com.twosoft.follow.data.managers

import android.util.Log
import com.twosoft.follow.data.models.local.GeoPoint
import java.util.ArrayList

/**
 * Created by robertofz on 12/30/18.
 */
object LocationManager {
    private val geoPoints: ArrayList<GeoPoint> = ArrayList<GeoPoint>()

    init {
        Log.d("ARRAY", "Location manager init")
    }

    fun addLocation(geoPoint: GeoPoint) {
        geoPoints.add(geoPoint)
        Log.d("ARRAY", "Length after add: " + this.geoPoints.size)
    }

    fun removeLocation(geoPoint: GeoPoint) {
        geoPoints.remove(geoPoint)
        Log.d("ARRAY", "Length after remove: " + this.geoPoints.size)
    }

    fun getPoints(): ArrayList<GeoPoint> {
        return geoPoints
    }
}