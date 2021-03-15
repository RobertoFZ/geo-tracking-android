package com.twosoft.follow.data.models.remote

import com.twosoft.follow.data.models.local.Profile

/**
 * Created by robertofz on 12/31/18.
 */
data class SignupData(
        val first_name: String,
        val last_name: String,
        val email: String,
        val password: String,
        val profile: Profile
)