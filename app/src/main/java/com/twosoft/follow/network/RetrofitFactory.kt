package com.twosoft.follow.network

import com.twosoft.follow.data.ApiConstants
import com.twosoft.follow.network.services.AuthenticationService
import com.twosoft.follow.network.services.UsersService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by robertofz on 12/30/18.
 */
class RetrofitFactory {
    companion object {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                        GsonConverterFactory.create())
                .baseUrl(ApiConstants.API_ENDPOINT)
                .build()

        fun createAuthenticationService(): AuthenticationService {
            return retrofit.create(AuthenticationService::class.java)
        }

        fun createUsersService(): UsersService {
            return retrofit.create(UsersService::class.java)
        }
    }
}