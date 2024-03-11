@file:Suppress("DEPRECATION")

package com.tommunyiri.dvtweatherapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Tom Munyiri on 26/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class LocationRepository(context: Context) {
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val _locationStateFlow = MutableStateFlow<LocationModel?>(null)
    val locationStateFlow: Flow<LocationModel?> = _locationStateFlow

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null,
        )
    }

    private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach {
                    setLocationData(it)
                }
            }
        }

    private fun setLocationData(location: Location) {
        _locationStateFlow.value =
            LocationModel(
                longitude = location.longitude,
                latitude = location.latitude,
            )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        val locationRequest: LocationRequest =
            LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
    }
}
