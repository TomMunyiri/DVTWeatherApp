package com.tommunyiri.dvtweatherapp.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.databinding.FragmentHomeBinding
import com.tommunyiri.dvtweatherapp.ui.BaseFragment
import com.tommunyiri.dvtweatherapp.ui.MainActivity
import com.tommunyiri.dvtweatherapp.utils.GPS_REQUEST_CHECK_SETTINGS
import com.tommunyiri.dvtweatherapp.utils.GpsUtil
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.convertCelsiusToFahrenheit
import com.tommunyiri.dvtweatherapp.utils.makeGone
import com.tommunyiri.dvtweatherapp.utils.makeVisible
import com.tommunyiri.dvtweatherapp.utils.observeOnce
import com.tommunyiri.dvtweatherapp.worker.UpdateWeatherWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private var isGPSEnabled = false

    @Inject
    lateinit var prefs: SharedPreferenceHelper

    private val viewModel by viewModels<HomeFragmentViewModel> { viewModelFactoryProvider }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        GpsUtil(requireContext()).turnGPSOn(object : GpsUtil.OnGpsListener {
            override fun gpsStatus(isGPSEnabled: Boolean) {
                this@HomeFragment.isGPSEnabled = isGPSEnabled
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        hideAllViews(true)
        observeViewModels()
        binding.swipeRefreshId.setOnRefreshListener {
            binding.errorText.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            hideViews()
            initiateRefresh()
            binding.swipeRefreshId.isRefreshing = false
        }
    }

    private fun observeViewModels() {
        with(viewModel) {
            weather.observe(viewLifecycleOwner) { weather ->
                weather?.let {
                    prefs.saveCityId(it.cityId)

                    if (prefs.getSelectedTemperatureUnit() == activity?.resources?.getString(R.string.temp_unit_fahrenheit))
                        it.networkWeatherCondition.temp =
                            convertCelsiusToFahrenheit(it.networkWeatherCondition.temp)

                    binding.weather = it
                    binding.networkWeatherDescription = it.networkWeatherDescription.first()
                }
            }

            dataFetchState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    true -> {
                        unHideViews()
                        binding.errorText.visibility = View.GONE
                    }

                    false -> {
                        hideViews()
                        binding.apply {
                            errorText.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            loadingText.visibility = View.GONE
                        }
                    }
                }
            }

            isLoading.observe(viewLifecycleOwner) { state ->
                when (state) {
                    true -> {
                        hideViews()
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            loadingText.visibility = View.VISIBLE
                        }
                    }

                    false -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            loadingText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun initiateRefresh() {
        viewModel.fetchLocationLiveData().observeOnce(
            viewLifecycleOwner
        ) { location ->
            viewModel.refreshWeather(location)
        }
    }

    private fun hideViews() {
        binding.apply {
            tvWeatherInText.makeGone()
            separator.makeGone()
            tvDateText.makeGone()
            tvWeatherTemperature.makeGone()
            tvWeatherMain.makeGone()
            tvMaxTemp.makeGone()
            tvMinTemp.makeGone()
            tvCurrentTemp.makeGone()
            ivCurrentWeather.makeGone()
        }
    }

    private fun unHideViews() {
        binding.apply {
            tvWeatherInText.makeVisible()
            separator.makeVisible()
            tvDateText.makeVisible()
            tvWeatherTemperature.makeVisible()
            tvWeatherMain.makeVisible()
            tvMinTemp.makeVisible()
            tvMaxTemp.makeVisible()
            tvCurrentTemp.makeVisible()
            ivCurrentWeather.makeVisible()
        }
    }

    private fun hideAllViews(state: Boolean) {
        if (state) {
            binding.apply {
                separator.makeGone()
                tvDateText.makeGone()
                tvWeatherTemperature.makeGone()
                tvWeatherMain.makeGone()
                errorText.makeGone()
                progressBar.makeGone()
                loadingText.makeGone()
                tvMaxTemp.makeGone()
                tvMinTemp.makeGone()
                tvCurrentTemp.makeGone()
                ivCurrentWeather.makeGone()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            allPermissionsGranted() -> {
                viewModel.fetchLocationLiveData().observeOnce(
                    viewLifecycleOwner
                ) { location ->
                    if (location != null) {
                        viewModel.getWeather(location)
                        setupWorkManager()
                    }
                }
            }

            shouldShowRequestPermissionRationale() -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.location_permission))
                    .setMessage(getString(R.string.access_location_message))
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { _, _ -> requireActivity().finish() }
                    .setPositiveButton(
                        getString(R.string.ask_me)
                    ) { _, _ ->
                        requestPermissions(REQUIRED_PERMISSIONS, LOCATION_REQUEST_CODE)
                    }
                    .show()
            }

            !isGPSEnabled -> {
                showShortSnackBar(getString(R.string.gps_required_message))
            }

            else -> {
                requestPermissions(REQUIRED_PERMISSIONS, LOCATION_REQUEST_CODE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).apply {
            //hideToolBar()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        isGPSEnabled = true
                        invokeLocationAction()
                    }

                    Activity.RESULT_CANCELED -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.enable_gps),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale() = REQUIRED_PERMISSIONS.all {
        shouldShowRequestPermissionRationale(it)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            invokeLocationAction()
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val LOCATION_REQUEST_CODE = 123
    }

    private fun setupWorkManager() {
        viewModel.fetchLocationLiveData().observeOnce(this) { prefs.saveLocation(it) }
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val weatherUpdateRequest =
            PeriodicWorkRequestBuilder<UpdateWeatherWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraint)
                .setInitialDelay(6, TimeUnit.HOURS)
                .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "Update_weather_worker",
            ExistingPeriodicWorkPolicy.REPLACE, weatherUpdateRequest
        )
    }
}