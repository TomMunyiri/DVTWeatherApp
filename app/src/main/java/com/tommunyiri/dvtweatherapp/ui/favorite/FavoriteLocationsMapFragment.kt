package com.tommunyiri.dvtweatherapp.ui.favorite

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.databinding.FragmentFavoriteLocationsMapBinding
import com.tommunyiri.dvtweatherapp.databinding.FragmentFavoritesBinding
import com.tommunyiri.dvtweatherapp.ui.BaseFragment
import com.tommunyiri.dvtweatherapp.ui.MainActivity
import com.tommunyiri.dvtweatherapp.utils.checkPlayServices
import com.tommunyiri.dvtweatherapp.utils.createLocationRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteLocationsMapFragment : BaseFragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraMoveStartedListener {
    private lateinit var binding: FragmentFavoriteLocationsMapBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private val requestCheckSettings = 1001
    private lateinit var mLastKnownLocation: Location
    private lateinit var task: Task<LocationSettingsResponse>
    private lateinit var locationCallback: LocationCallback
    private val DEFAULT_ZOOM = 5f
    private var firstLocationUpdate = true
    private val REQUEST_LOCATION_PERMISSION = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteLocationsMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapsFavoriteLocations.apply {
            onCreate(null)
            onResume()
            getMapAsync(this@FavoriteLocationsMapFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).apply {
            showToolBar()
            resetTransparentStatusBar()
            hideBottomNavigationBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).apply {
            showBottomNavigationBar()
        }
    }

    private fun populateFields() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(createLocationRequest())

        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        task = client.checkLocationSettings(builder.build())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                mLastKnownLocation = locationResult.lastLocation!!
                if (firstLocationUpdate) {
                    firstLocationUpdate = false
                    val place =
                        LatLng(mLastKnownLocation.latitude, mLastKnownLocation.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place, DEFAULT_ZOOM))
                }
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        populateFields()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkPermission()
        loadMapData()
    }

    private fun loadMapData() {
        val cities = listOf(
            Triple("Kampala", 0.3163, 32.5825),
            Triple("Nairobi", -1.2921, 36.8219),
            Triple("Kigali", -1.9441, 30.0595)
        )

        for ((cityName, lat, lng) in cities) {
            val markerOptions = MarkerOptions()
                .position(LatLng(lat, lng))
                .title(cityName)
            mMap.addMarker(markerOptions)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (isPermissionGranted()) {
            fusedLocationClient.requestLocationUpdates(
                createLocationRequest(), locationCallback, Looper.getMainLooper()
            )
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkPermission() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            if (checkPlayServices())
                checkSettings()
        } else {
            requestPermission()
        }
    }

    /***/
    private fun checkSettings() {
        task.addOnSuccessListener {
            requestLocationUpdates()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        requestCheckSettings
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
//                    showWarning(R.string.could_not_turn_on_location))
                }
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                mMap.isMyLocationEnabled = true
                requestLocationUpdates()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCheckSettings) {
                requestLocationUpdates()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteLocationsMapFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCameraMoveStarted(p0: Int) {
        TODO("Not yet implemented")
    }
}