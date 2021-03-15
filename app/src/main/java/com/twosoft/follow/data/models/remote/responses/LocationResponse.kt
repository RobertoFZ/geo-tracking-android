package com.twosoft.follow.data.models.remote.responses

import com.twosoft.follow.data.models.local.User

/**
 * Created by robertofz on 12/31/18.
 */
data class LocationResponse(
        val user: User,
        val address: String,
        val date: Float,
        val latitude: Double,
        val longitude: Double
)