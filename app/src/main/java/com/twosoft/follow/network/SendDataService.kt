package com.twosoft.follow.network

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.data.managers.LocationManager
import com.twosoft.follow.data.models.local.GeoPoint
import com.twosoft.follow.data.models.remote.responses.ErrorResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.HttpException
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by robertofz on 12/30/18.
 */
class SendDataService : BroadcastReceiver(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private var context: Context? = null
    private var preferencesHelper: PreferencesHelper? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        context = p0
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this.context!!)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }
        mGoogleApiClient?.connect()
        preferencesHelper = PreferencesHelper(this.context!!)
    }

    private var mLastLocation: Location? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this.context!!).lastLocation.addOnSuccessListener { location : Location? ->
            mLastLocation = location
            Log.d("Test", mLastLocation.toString())
            if (mLastLocation != null) {
                sendLocationToServer()
            }
        }
        /*
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient)
         */

        Log.d("LOCATION_SERVICE", "Connected to google services")
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun sendLocationToServer() {
        Log.d("LOCATION SERVICE", "Called sendLocationToServer")
        val token = preferencesHelper?.token
        val user_id = preferencesHelper?.pk
        val active = preferencesHelper?.on_route

        if (ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context!!, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient)
        if (mLastLocation != null) {
            Log.d("LOCATION_SERVICE COORS", mLastLocation?.latitude.toString() + ", " + mLastLocation?.longitude.toString())
            val userService = RetrofitFactory.createUsersService()
            // System.currentTimeMillis()
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            val locationData = GeoPoint(user_id!!, mLastLocation?.latitude!!, mLastLocation?.longitude!!, df.format(Date()))
            Log.d("LOCATION_SERVICE BODY", locationData.toString())
            userService.sendLocation( "Token $token", locationData).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> Log.d("LOCATION_SERVICE", result.toString()) },
                            { error -> onSendLocationError(error, locationData) }
                    )
        }

        val geoPoints = LocationManager.getPoints()
        geoPoints.indices
                .map { geoPoints[it] }
                .forEach {
                    RetrofitFactory.createUsersService().sendLocation( "Token $token", it).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { result -> LocationManager.removeLocation(it) },
                                    { error -> Log.d("LOCATION_SERVICE ERROR", error.message) }
                            )
                }
    }

    private fun onSendLocationError(error: Throwable, locationData: GeoPoint) {
        if (error is HttpException) {
            val body = error.response().errorBody()
            val errorConverter = RetrofitFactory.retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls<Annotation>(0))

            try {
                val errorResponse = errorConverter.convert(body)
                Log.d("LOCATION_SERVICE ERROR", errorResponse.toString())
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
        val geoPoints = LocationManager.getPoints()
        geoPoints.add(locationData)
    }

}