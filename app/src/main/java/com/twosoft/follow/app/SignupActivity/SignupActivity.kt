package com.twosoft.follow.app.SignupActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.twosoft.follow.MapActivity.MapActivity
import com.twosoft.follow.R
import com.twosoft.follow.app.LoginActivity.LoginActivity
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.network.RetrofitFactory
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by robertofz on 12/31/18.
 */
class SignupActivity : AppCompatActivity(), SignupContract.View {

    // Initialize Presenter (also Model in the constructor of Presenter) & has object of Presenter
    private lateinit var signupPresenter: SignupPresenter

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

    override fun sendToMainApp() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showLoader() {
        progressBar.visibility = View.VISIBLE
        buttonSignup.visibility = View.GONE
        link_login.visibility = View.GONE
    }

    override fun hideLoader() {
        progressBar.visibility = View.GONE
        buttonSignup.visibility = View.VISIBLE
        link_login.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Init presenter
        signupPresenter = SignupPresenter(this, RetrofitFactory.createAuthenticationService(), PreferencesHelper(this))

        buttonSignup.setOnClickListener { view ->
            doLogin()
        }
        link_login.setOnClickListener { view ->
            val intent =  Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun doLogin() {
        val first_name = editTextFirstName.text.toString()
        val last_name = editTextLastName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        val confirmPassword = editTextConfirmPassword.text.toString()
        val phone = editTextPhone.text.toString()
        val municipality = editTextMunicipality.text.toString()
        signupPresenter.doSignup(first_name, last_name, email, password, confirmPassword, phone, municipality)
    }
}