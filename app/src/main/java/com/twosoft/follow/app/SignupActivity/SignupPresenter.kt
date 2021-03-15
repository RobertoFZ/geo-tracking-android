package com.twosoft.follow.app.SignupActivity

import android.util.Log
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.data.models.local.Profile
import com.twosoft.follow.data.models.local.User
import com.twosoft.follow.data.models.remote.SignupData
import com.twosoft.follow.data.models.remote.responses.ErrorResponse
import com.twosoft.follow.data.models.remote.responses.LoginResponse
import com.twosoft.follow.network.RetrofitFactory
import com.twosoft.follow.network.services.AuthenticationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by robertofz on 12/31/18.
 */
class SignupPresenter(
        private var signupView: SignupContract.View?,
        private val authenticationService: AuthenticationService,
        private val preferencesHelper: PreferencesHelper
)
    : SignupContract.UserActionListener {

    override fun doSignup(first_name: String, last_name: String, email: String, password: String, phone: String, municipality: String) {
        val profile = Profile("es", phone, municipality)
        val signupData = SignupData(first_name, last_name, email, password, profile)
        signupView?.showLoader()
        signupView?.hideKeyboard()
        authenticationService.signup(signupData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> onSignup(result) },
                        { error -> onSignupError(error) }
                )
    }

    // Destroy View when Activity destroyed
    fun onDestroy() {
        signupView = null
    }

    private fun onSignup(user: User) {
        Log.d("SIGNUP_PRESENTER", user.toString())
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
        signupView?.sendToMainApp()
    }

    private fun onSignupError(error: Throwable) {
        signupView?.hideLoader()
        if (error is HttpException) {
            val body = error.response().errorBody()
            val errorConverter = RetrofitFactory.retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls<Annotation>(0))
            Log.d("SIGN UP PRESENTER", body?.string())
            try {
                val errorResponse = errorConverter.convert(body)
                signupView?.showError(errorResponse.message)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
    }
}