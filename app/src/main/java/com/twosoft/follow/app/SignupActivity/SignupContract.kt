package com.twosoft.follow.app.SignupActivity

/**
 * Created by robertofz on 12/31/18.
 */
class SignupContract {

    interface UserActionListener {
        fun doSignup(first_name: String, last_name: String, email: String, password: String, phone: String, municipality: String)
    }

    interface View {
        fun sendToMainApp()
        fun showLoader()
        fun hideLoader()
        fun hideKeyboard()
        fun showError(message: String)
    }
}