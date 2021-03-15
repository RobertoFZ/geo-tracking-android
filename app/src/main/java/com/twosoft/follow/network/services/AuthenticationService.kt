package com.twosoft.follow.network.services

import com.twosoft.follow.data.ApiConstants
import com.twosoft.follow.data.models.local.User
import com.twosoft.follow.data.models.remote.LoginData
import com.twosoft.follow.data.models.remote.SignupData
import com.twosoft.follow.data.models.remote.responses.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by robertofz on 12/30/18.
 */
interface AuthenticationService {

    @POST(ApiConstants.LOGIN_ENDPOINT)
    fun login(@Body loginData: LoginData): Observable<User>

    @POST(ApiConstants.USERS_SIGNUP)
    fun signup(@Body signupData: SignupData): Observable<User>

}