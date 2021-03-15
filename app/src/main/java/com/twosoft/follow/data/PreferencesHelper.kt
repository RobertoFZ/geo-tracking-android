package com.twosoft.follow.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by robertofz on 12/30/18.
 */
class PreferencesHelper(context: Context) {
    companion object {
        val ENABLE_FOLLOW: Boolean = true
        private const val PK = "data.source.prefs.PK"
        private const val TOKEN = "data.source.prefs.TOKEN"
        private const val LOCALE = "data.source.pref.LOCALE"
        private const val PHONE = "data.source.pref.PHONE"
        private const val FIRST_NAME = "data.source.prefs.FIRST_NAME"
        private const val LAST_NAME = "data.source.prefs.LAST_NAME"
        private const val EMAIL = "data.source.prefs.EMAIL"
        private const val FOLLOW_MODE = "data.source.prefs.FOLLOW_MODE"
        private const val IS_ACTIVE = "data.source.prefs.IS_ACTIVE"
        private const val MUNICIPALITY = "data.source.prefs.MUNICIPALITY"
        private const val ON_ROUTE = "data.source.prefs.ON_ROUTE"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    var follow_mode = preferences.getBoolean(FOLLOW_MODE, ENABLE_FOLLOW)
        set(value) = preferences.edit().putBoolean(FOLLOW_MODE, ENABLE_FOLLOW).apply()
    var pk = preferences.getInt(PK, 0)
        set(value) = preferences.edit().putInt(PK, value).apply()
    var email = preferences.getString(EMAIL, "")
        set(value) = preferences.edit().putString(EMAIL, value).apply()
    var first_name = preferences.getString(FIRST_NAME, "")
        set(value) = preferences.edit().putString(FIRST_NAME, value).apply()
    var last_name = preferences.getString(LAST_NAME, "")
        set(value) = preferences.edit().putString(LAST_NAME, value).apply()
    var token = preferences.getString(TOKEN, "")
        set(value) = preferences.edit().putString(TOKEN, value).apply()
    var phone = preferences.getString(PHONE, "")
        set(value) = preferences.edit().putString(PHONE, value).apply()
    var locale = preferences.getString(LOCALE, "")
        set(value) = preferences.edit().putString(LOCALE, value).apply()
    var is_active = preferences.getBoolean(IS_ACTIVE, false)
        set(value) = preferences.edit().putBoolean(IS_ACTIVE, value).apply()
    var municipality = preferences.getString(MUNICIPALITY, "false")
        set(value) = preferences.edit().putString(MUNICIPALITY, value).apply()
    var on_route = preferences.getBoolean(ON_ROUTE, false)
        set(value) = preferences.edit().putBoolean(ON_ROUTE, value).apply()

    public fun clearPreferences() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.commit()
    }
}