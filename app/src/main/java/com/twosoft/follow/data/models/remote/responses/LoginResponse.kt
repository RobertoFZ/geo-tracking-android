package com.twosoft.follow.data.models.remote.responses

import com.twosoft.follow.data.models.local.User

/**
 * Created by robertofz on 12/30/18.
 */
data class LoginResponse(
        val user : User,
        val token: String,
        val facebook_login: Boolean
)