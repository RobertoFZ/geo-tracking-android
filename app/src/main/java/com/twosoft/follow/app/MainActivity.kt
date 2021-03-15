package com.twosoft.follow.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.twosoft.follow.MapActivity.MapActivity
import com.twosoft.follow.R
import com.twosoft.follow.app.LoginActivity.LoginActivity
import com.twosoft.follow.data.PreferencesHelper


class MainActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val preferencesHelper = PreferencesHelper(this)
            if (preferencesHelper.token != "") {
                intent = Intent(this, MapActivity::class.java)
            } else {
                //intent = Intent(this, MapActivity::class.java)
                intent = Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

}