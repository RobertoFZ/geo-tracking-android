package com.twosoft.follow.app.LoginActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.twosoft.follow.MapActivity.MapActivity

import com.twosoft.follow.R
import com.twosoft.follow.app.SignupActivity.SignupActivity
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.network.RetrofitFactory
import kotlinx.android.synthetic.main.activity_login.*
import android.app.Activity
import android.view.inputmethod.InputMethodManager


/**
 * Created by robertofz on 12/30/18.
 */

class LoginActivity : AppCompatActivity(), LoginContract.View {

    // Initialize Presenter (also Model in the constructor of Presenter) & has object of Presenter
    private lateinit var loginPresenter: LoginPresenter

    override fun sendToMainApp() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = getCurrentFocus()
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }

    override fun showLoader() {
        progressBar.visibility = View.VISIBLE
        buttonLogin.visibility = View.GONE
        link_signup.visibility = View.GONE
    }

    override fun hideLoader() {
        progressBar.visibility = View.GONE
        buttonLogin.visibility = View.VISIBLE
        link_signup.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Init presenter
        loginPresenter = LoginPresenter(this, RetrofitFactory.createAuthenticationService(), PreferencesHelper(this))

        buttonLogin.setOnClickListener { view ->
            doLogin()
        }

        link_signup.setOnClickListener { view ->
            val intent =  Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun doLogin() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        loginPresenter.doLogin(email, password)
    }
}