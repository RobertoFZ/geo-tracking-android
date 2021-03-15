package com.twosoft.follow.network.services

import com.google.android.gms.vision.barcode.Barcode
import com.twosoft.follow.data.ApiConstants
import com.twosoft.follow.data.models.local.GeoPoint
import com.twosoft.follow.data.models.local.User
import com.twosoft.follow.data.models.remote.LocationData
import com.twosoft.follow.data.models.remote.StatusData
import com.twosoft.follow.data.models.remote.responses.LocationResponse
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by robertofz on 12/31/18.
 */
interface UsersService {

    @PATCH(ApiConstants.USERS_UPDATE_STATUS)
    fun updateStatus(
            @Path(ApiConstants.PK) user_pk: Int,
            @Header("Authorization") authHeader: String,
            @Body statusData: StatusData
    ): Observable<User>

    @POST(ApiConstants.USERS_LOCATIONS)
    fun sendLocation(
            @Header("Authorization") authHeader: String,
            @Body locationData: GeoPoint
    ): Observable<GeoPoint>

    @GET(ApiConstants.USERS_FOLLOWS)
    fun getFollowUsers(
            @Path(ApiConstants.PK) user_pk: Int,
            @Header("Authorization") authHeader: String
    ): Observable<Array<LocationResponse>>

}