package com.twosoft.follow.MapActivity

import android.util.Log
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.data.models.local.User
import com.twosoft.follow.data.models.remote.StatusData
import com.twosoft.follow.data.models.remote.responses.ErrorResponse
import com.twosoft.follow.data.models.remote.responses.LocationResponse
import com.twosoft.follow.network.RetrofitFactory
import com.twosoft.follow.network.services.UsersService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.HttpException
import java.io.IOException

/**
 * Created by robertofz on 12/30/18.
 */

// Presenter has the object of both View and Model(Interactor)
// Implements OnFinishedListener to listen for Interactor response

class MapPresenter(
        private var mapView: MapContract.View?,
        private var preferencesHelper: PreferencesHelper,
        private var usersService: UsersService
) : MapContract.UserActionsListener {
    private val DEFAULT_LATITUDE: Double = 20.966334
    private val DEFAULT_LONGITUDE: Double = (-89.622949)

    override fun centerMap() {
        mapView?.centerMap(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    override fun userStart() {
        val userName: String = preferencesHelper.first_name
        val message = "Hola $userName"
        mapView?.showToast(message)
    }

    override fun updateStatus(user_id: Int, status: Boolean) {
        mapView?.showLoader()
        val statusData = StatusData(status)
        usersService.updateStatus( user_id,"Token ${preferencesHelper.token}", statusData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> onStatusChange(result) },
                        { error -> onStatusChangeError(error) }
                )
    }

    override fun getLocations() {
        mapView?.showLoader()

        usersService.getFollowUsers(preferencesHelper.pk, "Token ${preferencesHelper.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> onGetLocations(result) },
                        { error -> onGetLocationError(error) }
                )

    }

    // Destroy View when Activity destroyed
    fun onDestroy() {
        mapView = null
    }

    private fun onGetLocations(geoPoints: Array<LocationResponse>) {
        mapView?.showLocations(geoPoints)
        mapView?.hideLoader()
    }

    private fun onGetLocationError(error: Throwable) {
        Log.d("MAP_PRESENTER", error.toString())
        if (error is HttpException) {
            val body = error.response().errorBody()
            val errorConverter = RetrofitFactory.retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls<Annotation>(0))

            try {
                val errorResponse = errorConverter.convert(body)
                mapView?.showToast(errorResponse.message)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
        mapView?.hideLoader()
    }

    private fun onStatusChange(user: User) {
        mapView?.setStatus(user.on_route)
        mapView?.hideLoader()
    }

    private fun onStatusChangeError(error: Throwable) {
        Log.d("MAP_PRESENTER", error.toString())
        if (error is HttpException) {
            val body = error.response().errorBody()
            val errorConverter = RetrofitFactory.retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls<Annotation>(0))

            try {
                val errorResponse = errorConverter.convert(body)
                Log.d("MAP_PRESENTER_ERROR", errorResponse.toString())
                if (error.code() == 401) {
                    mapView?.showToast("Tu sesión expiró, inicia sesión de nuevo")
                    mapView?.logout()
                    return
                }
                if( errorResponse.message != null) {
                    mapView?.showToast(errorResponse.message)
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
        mapView?.hideLoader()
    }
}