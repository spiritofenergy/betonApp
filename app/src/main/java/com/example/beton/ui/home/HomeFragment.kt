package com.example.beton.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.beton.HomeActivity
import com.example.beton.OrderActivity
import com.example.beton.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var home: HomeActivity
    private var isPermissionsGet: Boolean = false

    private var lat: Double = 0.0
    private var lon: Double = 0.0

    val point: LatLng = LatLng(55.9207, 37.9748)
    private val factory: Location = Location("")

    private var mMap: GoogleMap? = null

    private lateinit var root: View

    private lateinit var floatingActionButton: FloatingActionButton

    private var distance: Float? = null
    private lateinit var addressMarker: String

    private lateinit var address: TextView

    private val markerOptions = MarkerOptions()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)

        if (activity != null) {
            home = activity as HomeActivity
        }

        address = root.findViewById(R.id.mapAddress)

        factory.latitude = point.latitude
        factory.longitude = point.longitude

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(home)

        floatingActionButton = root.findViewById(R.id.floatingActionButton)
        floatingActionButton.isEnabled = false

        floatingActionButton.setOnClickListener {
            val intent = Intent(home, OrderActivity::class.java)
            intent.putExtra("address", addressMarker)
            intent.putExtra("dist", distance)
            startActivity(intent)
        }

        val supportMap = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMap.getMapAsync(this)

        getLocationPermission()

        return root
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                home,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                home,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(home, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101);
        } else {
            isPermissionsGet = true
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        updateMap()

        getDeviceLocation()

        mMap?.setOnMapClickListener {

            markerOptions.position(it)

            Log.d("chords", "${it.latitude}/${it.longitude}")

            addressMarker = getAddress(it)
            address.text = addressMarker

            val locationTo = Location("")
            locationTo.latitude = it.latitude
            locationTo.longitude = it.longitude

            distance = factory.distanceTo(locationTo)

            mMap!!.clear()
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(
                it, 15f
            )
            mMap!!.animateCamera(cameraUpdate)
            mMap!!.addMarker(markerOptions)

            floatingActionButton.isEnabled = true
        }

    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        if (isPermissionsGet) {
            val task = fusedLocationClient.lastLocation

            task.addOnSuccessListener { location ->
                if (task.isSuccessful) {
                    lat = location.latitude
                    lon = location.longitude

                    val latLng = LatLng(lat, lon)

                    val locationTo = Location("")
                    locationTo.latitude = lat
                    locationTo.longitude = lon

                    addressMarker = getAddress(latLng)
                    address.text = addressMarker

                    distance = factory.distanceTo(locationTo)

                    mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))

                    markerOptions.position(latLng)
                    mMap?.addMarker(markerOptions)

                    floatingActionButton.isEnabled = true
                } else {
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(null, 15F))
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }

        } else {
            getLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        isPermissionsGet = false
        when(requestCode) {
            101 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionsGet = true
                }
            }
        }

        updateMap()
    }

    private fun updateMap() {
        if (mMap != null) {
            try {
                if (isPermissionsGet) {
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = true
                } else {
                    mMap?.isMyLocationEnabled = false
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
                    getLocationPermission()
                }

            } catch (e: SecurityException) {
                Log.e("Exception: ", "${e.message}")
            }
        }
    }

    private fun getAddress(latLng: LatLng) : String {
        val aLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.Builder().setLanguage("RU").setScript("Latn").setRegion("RS").build()
        } else {
            Locale("RU")
        }

        val addresses: List<Address>
        val geocoder = Geocoder(home, aLocale)

        return try {
            addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
            val address: String = addresses[0]
                .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            //val city: String = addresses[0].locality
            //val state: String = addresses[0].adminArea
            //val country: String = addresses[0].countryName
            //val postalCode: String = addresses[0].postalCode
            //val knownName: String = addresses[0].featureName


            address
        } catch (e: IOException) {
            e.printStackTrace()
            "No Address Found"
        }
    }

//    private fun getDistance(latLngFrom: LatLng, latLngTo: LatLng) : Float {
//        val result: Float = 0.0F
//        Location.distanceBetween(latLngFrom.latitude, latLngFrom.longitude, latLngTo.latitude, latLngTo.longitude, results)
//        return
//    }
}