package com.twosoft.follow.app.LoginActivity

import com.twosoft.follow.data.models.remote.responses.LoginResponse

/**
 * Created by robertofz on 12/30/18.
 */
class LoginContract {

    interface UserActionListener {
        fun doLogin(email: String, password: String)
    }

    interface View {
        fun sendToMainApp()
        fun showLoader()
        fun hideLoader()
        fun hideKeyboard()
        fun showError(message: String)
    }
}