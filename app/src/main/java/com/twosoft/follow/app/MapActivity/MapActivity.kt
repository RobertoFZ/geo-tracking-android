package com.twosoft.follow.MapActivity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.twosoft.follow.R
import com.twosoft.follow.app.LoginActivity.LoginActivity
import com.twosoft.follow.data.PreferencesHelper
import com.twosoft.follow.data.models.remote.responses.LocationResponse
import com.twosoft.follow.network.RetrofitFactory
import com.twosoft.follow.network.SendDataService
import kotlinx.android.synthetic.main.activity_map.*
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, MapContract.View {
    private val DEFAULT_ZOOM: Float = 12f
    private val MAP_UPDATE_TIME: Long = 30000 // miliseconds
    private val SEND_DATA_TIME: Long = 60 // seconds
    private val SEND_DATA_SERVICE_ID = 0
    private var sendDataServiceIntent: Intent? = null
    private var sendDataServicePIntent: PendingIntent? = null
    private var alarm: AlarmManager? = null
    private var runnableCode: Runnable? = null
    private lateinit var mMap: GoogleMap

    // Initialize Presenter (also Model in the constructor of Presenter) & has object of Presenter
    private lateinit var mapPresenter: MapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Init presenter
        mapPresenter = MapPresenter(this, PreferencesHelper(this), RetrofitFactory.createUsersService())
        mapPresenter.userStart()

        buttonLogout.setOnClickListener {
            this.logout()
        }

        val user_id = PreferencesHelper(this).pk

        switchActive.setOnCheckedChangeListener { _, isChecked ->
            mapPresenter.updateStatus(user_id, isChecked)
        }
        switchActive.isChecked = PreferencesHelper(this).on_route
    }

    override fun onDestroy() {
        // Destroy View
        mapPresenter.onDestroy()
        super.onDestroy()
    }

    override fun setStatus(status: Boolean) {
        switchActive.isChecked = status

        if (status) {
            switchActive.text = "Activo"
        } else {
            switchActive.text = "Inactivo"
        }
        PreferencesHelper(this).on_route = status
    }

    override fun centerMap(latitude: Double, longitude: Double) {
        val location = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
    }

    override fun showLocations(locations: Array<LocationResponse>) {
        locations.iterator().forEach { location -> createMarker(location) }
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showLoader() {
        Log.d("MAP_ACTIVITY", "Loading...")
    }

    override fun hideLoader() {
        Log.d("MAP_ACTIVITY", "Stop loading...")
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    setUpMap()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        setUpMap()
    }

    override fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Quieres cerrar sesión?")
        builder.setPositiveButton("Sí") { dialog, which ->
            doLogout()
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->

        }
        builder.show()

        /*
        with(builder)
        {
            setTitle("")
            setPositiveButton("Sí", DialogInterface.OnClickListener { dialogInterface, i -> doLogout()  })
            setNegativeButton("Cancelar", null)
            show()
        }
         */
    }

    private fun doLogout() {
        PreferencesHelper(this).clearPreferences()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        mapPresenter.centerMap()

        // mapPresenter.getLocations()
        // startMapUpdateTask()
        startLocationService()
    }

    private fun startLocationService() {
        Log.d("MAP_ACTIVITY", "start alarm system")
        sendDataServiceIntent = Intent(this, SendDataService::class.java)
        sendDataServicePIntent = PendingIntent.getBroadcast(this, SEND_DATA_SERVICE_ID, sendDataServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarm?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SEND_DATA_TIME * 1000, sendDataServicePIntent)
    }

    private fun createMarker(appMarker: LocationResponse) {
        val title = "${appMarker.user.first_name} ${appMarker.user.last_name}"
        val date = getDateTime((appMarker.date * 1000).toLong())
        val location = LatLng(appMarker.latitude, appMarker.longitude)
        val marker = MarkerOptions().position(location).title(title.plus(" $date")).snippet("${appMarker.address}")
        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
    }

    private fun startMapUpdateTask() {
        Log.d("MAP_ACTIVITY", "start handler system")
        val handler = Handler()
        handler.postDelayed(object : Runnable {

            override fun run() {
                mapPresenter.getLocations()
                handler.postDelayed(this, MAP_UPDATE_TIME)
            }
        }, MAP_UPDATE_TIME)
    }

    private fun getDateTime(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy h:m a")
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
