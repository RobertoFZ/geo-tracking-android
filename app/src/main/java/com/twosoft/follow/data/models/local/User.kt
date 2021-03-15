package com.twosoft.follow.data.models.local

/**
 * Created by robertofz on 12/30/18.
 */
data class User(
        val id: Int,
        val first_name: String,
        val last_name: String,
        val email: String,
        val is_active: Boolean,
        val on_route: Boolean,
        val token: String,
        val role: String,
        val profile: Profile
) {
    var name: String = first_name.plus(" ").plus(last_name)
}