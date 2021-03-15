package com.twosoft.follow.MapActivity

import com.google.android.gms.maps.model.LatLng
import com.twosoft.follow.data.models.remote.responses.LocationResponse

/**
 * Created by robertofz on 12/30/18.
 */

class MapContract {

    interface UserActionsListener {
        fun getLocations()
        fun userStart()
        fun centerMap()
        fun updateStatus(user_id: Int, status: Boolean)
    }

    interface View {
        fun centerMap(latitude: Double, longitude: Double)
        fun showLocations(locations: Array<LocationResponse>)
        fun showLoader()
        fun hideLoader()
        fun showToast(message: String)
        fun logout()
        fun setStatus(status: Boolean)
    }

}