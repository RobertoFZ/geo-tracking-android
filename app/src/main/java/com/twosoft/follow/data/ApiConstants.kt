package com.twosoft.follow.data

import org.jetbrains.annotations.NotNull

/**
 * Created by robertofz on 12/30/18.
 */
class ApiConstants {
    companion object {
        //const val API_ENDPOINT = "http://192.168.0.11:8000/api/v1/"
        const val API_ENDPOINT = "http://138.68.29.90:8000/api/v1/"

        // KEYS
        const val PK = "pk"

        // AUTHENTICATION
        const val LOGIN_ENDPOINT = API_ENDPOINT.plus("auth/login")

        // USERS
        private const val USERS_ENDPOINT = API_ENDPOINT.plus("users/")
        const val USERS_SIGNUP = USERS_ENDPOINT
        const val USERS_UPDATE_STATUS = USERS_ENDPOINT.plus("{$PK}")

        // GEOLOCATION
        const val USERS_LOCATIONS = API_ENDPOINT.plus("locations/")
        const val USERS_FOLLOWS = USERS_ENDPOINT.plus("{$PK}/follows/")

    }
}