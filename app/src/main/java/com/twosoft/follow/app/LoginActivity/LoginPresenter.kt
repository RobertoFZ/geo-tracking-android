package com.twosoft.follow.app.LoginActivity

import android.util.Log
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.data.models.local.User
import com.twosoft.follow.data.models.remote.LoginData
import com.twosoft.follow.data.models.remote.responses.ErrorResponse
import com.twosoft.follow.data.models.remote.responses.LoginResponse
import com.twosoft.follow.network.RetrofitFactory
import com.twosoft.follow.network.services.AuthenticationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.HttpException
import java.io.IOException


/**
 * Created by robertofz on 12/30/18.
 */

class LoginPresenter(
        private var loginView: LoginContract.View?,
        private val authenticationService: AuthenticationService,
        private val preferencesHelper: PreferencesHelper
)
    : LoginContract.UserActionListener {

    override fun doLogin(email: String, password: String) {
        val loginData = LoginData(email, password)
        loginView?.showLoader()
        loginView?.hideKeyboard()
        authenticationService.login(loginData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> onLogin(result) },
                        { error -> onLoginError(error) }
                )
    }

    // Destroy View when Activity destroyed
    fun onDestroy() {
        loginView = null
    }

    private fun onLogin(user: User) {
        Log.d("LOGIN_PRESENTER", user.toString())
        preferencesHelper.pk = user.id
        preferencesHelper.first_name = user.first_name
        preferencesHelper.last_name = user.last_name
        preferencesHelper.email = user.email
        preferencesHelper.token = user.token
        preferencesHelper.is_active = user.is_active
        preferencesHelper.phone = user.profile.phone
        preferencesHelper.locale = user.profile.locale
        preferencesHelper.municipality = user.profile.municipality
        preferencesHelper.on_route = user.on_route
        loginView?.sendToMainApp()
    }

    private fun onLoginError(error: Throwable) {
        loginView?.hideLoader()
        if (error is HttpException) {
            val body = error.response().errorBody()
            val errorConverter = RetrofitFactory.retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls<Annotation>(0))

            try {
                val errorResponse = errorConverter.convert(body)
                loginView?.showError(errorResponse.message)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
    }
}