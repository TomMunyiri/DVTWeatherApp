package com.tommunyiri.dvtweatherapp.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
    private val weatherForecastAdapter by lazy { WeatherForecastAdapter() }

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

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
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
        observeMoreViewModels()
        binding.swipeRefreshId.setOnRefreshListener {
            binding.errorText.makeGone()
            binding.progressBar.makeVisible()
            hideViews()
            binding.swipeRefreshId.setBackgroundColor(Color.TRANSPARENT)
            initiateRefresh()
            binding.swipeRefreshId.isRefreshing = false
        }
    }

    private fun getWeatherForecast() {
        //viewModel.getWeatherForecast(prefs.getCityId())
        binding.rvForecast.adapter = weatherForecastAdapter
        viewModel.fetchLocationLiveData().observeOnce(
            viewLifecycleOwner
        ) { location ->
            viewModel.isWeatherRefresh.observe(viewLifecycleOwner) { isWeatherRefresh ->
                if (isWeatherRefresh) {
                    viewModel.refreshForecastData(location)
                } else {
                    viewModel.getWeatherForecast(location)
                }
            }
        }
    }

    private fun observeMoreViewModels() {
        with(viewModel) {
            forecast.observe(viewLifecycleOwner) { weatherForecast ->
                weatherForecast?.let { list ->
                    weatherForecast.forEach {
                        if (prefs.getSelectedTemperatureUnit() == requireActivity().resources.getString(
                                R.string.temp_unit_fahrenheit
                            )
                        )
                            it.networkWeatherCondition.temp =
                                convertCelsiusToFahrenheit(it.networkWeatherCondition.temp)
                    }
                    weatherForecastAdapter.submitList(list)
                }
            }

            dataFetchStateForecast.observe(viewLifecycleOwner) { state ->
                when (state) {
                    true -> {
                        binding.apply {
                            rvForecast.isVisible = state
                            forecastErrorText.isVisible = !state
                        }
                    }

                    false -> {
                        binding.apply {
                            Toast.makeText(
                                context,
                                "Error fetching weather forecast",
                                Toast.LENGTH_SHORT
                            ).show()
                            rvForecast.makeGone()
                        }
                    }
                }

            }

            isForecastLoading.observe(viewLifecycleOwner) { state ->
                when (state) {
                    true -> {
                        binding.pbForecast.makeVisible()
                    }

                    false -> {
                        binding.pbForecast.makeGone()
                    }
                }
            }

        }
    }

    private fun adaptUIWithCurrentWeather(condition: String?) {
        if (condition != null) {
            when {
                condition.contains("cloud", true) -> {
                    binding.apply {
                        swipeRefreshId.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.cloudy)
                        )
                        ivCurrentWeather.setImageResource(R.drawable.forest_cloudy)
                    }
                }

                condition.contains("rain", true)
                        || condition.contains("snow", true)
                        || condition.contains("mist", true)
                        || condition.contains("haze", true) -> {
                    binding.apply {
                        swipeRefreshId.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.rainy)
                        )
                        ivCurrentWeather.setImageResource(R.drawable.forest_rainy)
                    }
                }

                else -> {
                    binding.apply {
                        swipeRefreshId.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.sunny)
                        )
                        ivCurrentWeather.setImageResource(R.drawable.forest_sunny)
                    }
                }
            }
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
                    adaptUIWithCurrentWeather(it.networkWeatherDescription.first().main)
                    getWeatherForecast()
                }
            }

            dataFetchState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    true -> {
                        unHideViews()
                        binding.errorText.makeGone()
                    }

                    false -> {
                        hideViews()
                        binding.apply {
                            errorText.makeVisible()
                            progressBar.makeGone()
                            loadingText.makeGone()
                        }
                    }
                }
            }

            isLoading.observe(viewLifecycleOwner) { state ->
                when (state) {
                    true -> {
                        hideViews()
                        binding.apply {
                            progressBar.makeVisible()
                            loadingText.makeVisible()
                        }
                    }

                    false -> {
                        binding.apply {
                            progressBar.makeGone()
                            loadingText.makeGone()
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
            rvForecast.makeGone()
            pbForecast.makeGone()
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
                rvForecast.makeGone()
                pbForecast.makeGone()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            allPermissionsGranted() -> {
                viewModel.fetchLocationLiveData().observeOnce(
                    viewLifecycleOwner
                ) { location ->
                    viewModel.getWeather(location)
                    setupWorkManager()
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
            hideToolBar()
            setTransparentStatusBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).apply {
            resetTransparentStatusBar()
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
        private val REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        private const val LOCATION_REQUEST_CODE = 123
    }

    private fun setupWorkManager() {
        viewModel.fetchLocationLiveData().observeOnce(this) { prefs.saveLocation(it) }
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val weatherUpdateRequest =
            PeriodicWorkRequestBuilder<UpdateWeatherWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DVT_update_weather_worker",
            ExistingPeriodicWorkPolicy.UPDATE, weatherUpdateRequest
        )
    }
}