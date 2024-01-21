package com.tommunyiri.dvtweatherapp.ui.favorite

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.data.model.Weather
import com.tommunyiri.dvtweatherapp.databinding.FragmentFavoritesBinding
import com.tommunyiri.dvtweatherapp.databinding.FragmentSearchDetailBinding
import com.tommunyiri.dvtweatherapp.ui.BaseBottomSheetDialog
import com.tommunyiri.dvtweatherapp.ui.BaseFragment
import com.tommunyiri.dvtweatherapp.ui.MainActivity
import com.tommunyiri.dvtweatherapp.ui.home.HomeFragmentViewModel
import com.tommunyiri.dvtweatherapp.ui.home.WeatherForecastAdapter
import com.tommunyiri.dvtweatherapp.ui.search.SearchFragmentViewModel
import com.tommunyiri.dvtweatherapp.ui.search.SearchResultAdapter
import com.tommunyiri.dvtweatherapp.utils.convertCelsiusToFahrenheit
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(), FavoriteLocationsAdapter.OnItemClickedListener {
    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel by viewModels<FavoriteFragmentViewModel> { viewModelFactoryProvider }
    private val searchViewModel by viewModels<SearchFragmentViewModel> { viewModelFactoryProvider }
    private lateinit var searchDetailBinding: FragmentSearchDetailBinding
    private lateinit var selectedCity: FavoriteLocation
    private val favoriteLocationsAdapter by lazy { FavoriteLocationsAdapter(this) }
    private val bottomSheetDialog by lazy {
        BaseBottomSheetDialog(
            requireActivity(),
            R.style.AppBottomSheetDialogTheme
        )
    }

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
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        searchDetailBinding = FragmentSearchDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).apply {
            showToolBar()
            resetTransparentStatusBar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.rvFavoriteLocations.adapter = favoriteLocationsAdapter
        viewModel.getFavoriteLocations()
        observeMoreViewModels()
        observeSearchViewModel()

        searchDetailBinding.fabClose.setOnClickListener {
            if (bottomSheetDialog.isShowing)
                bottomSheetDialog.dismiss()
        }

        searchDetailBinding.ivFavorite.setOnClickListener {
            //viewModel.saveFavoriteLocation(selectedCity)
        }
    }

    private fun observeSearchViewModel() {
        with(searchViewModel) {

            weatherInfo.observe(viewLifecycleOwner) { weather ->
                weather?.let {
                    val formattedWeather = it.apply {
                        this.networkWeatherCondition.temp =
                            convertKelvinToCelsius(this.networkWeatherCondition.temp)
                    }
                    displayWeatherResult(formattedWeather)
                }
            }

            isLoading.observe(viewLifecycleOwner) { state ->
                binding.searchWeatherLoader.isVisible = state
            }

            dataFetchState.observe(viewLifecycleOwner) { state ->
                if (!state) {
                    Snackbar.make(
                        requireView(),
                        "An error occurred! Please try again.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun observeMoreViewModels() {
        with(viewModel) {
            favoriteLocation.observe(viewLifecycleOwner) { favoriteLocation ->
                favoriteLocation?.let { list ->
                    favoriteLocationsAdapter.submitList(list)
                }
            }

            dataFetchStateFavoriteLocations.observe(viewLifecycleOwner) { state ->
                binding.apply {
                    rvFavoriteLocations.isVisible = state
                    tvZeroFavorites.isVisible = !state
                }
            }

            isFavoriteLocationLoading.observe(viewLifecycleOwner) { state ->
                binding.searchWeatherLoader.isVisible = state
            }

        }
    }

    private fun displayWeatherResult(result: Weather) {
        with(searchDetailBinding) {
            weatherCondition = result.networkWeatherDescription.first()
            location.text = result.name
            weather = result
            selectedCity = FavoriteLocation(result.name)
            val condition = weatherCondition as NetworkWeatherDescription
            adaptUIWithCurrentWeather(condition.main)
        }

        with(bottomSheetDialog) {
            setCancelable(true)
            setContentView(searchDetailBinding.root)
            show()
        }
    }

    private fun adaptUIWithCurrentWeather(condition: String?) {
        if (condition != null) {
            when {
                condition.contains("cloud", true) -> {
                    searchDetailBinding.cardView.background.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.cloudy
                        ), PorterDuff.Mode.SRC_ATOP
                    );

                }

                condition.contains("rain", true)
                        || condition.contains("snow", true)
                        || condition.contains("mist", true)
                        || condition.contains("haze", true) -> {
                    searchDetailBinding.cardView.background.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.rainy
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                }

                else -> {
                    searchDetailBinding.cardView.background.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.sunny
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onSearchResultClicked(dbFavoriteLocation: FavoriteLocation) {
        searchViewModel.getSearchWeather(dbFavoriteLocation.name)
        selectedCity = dbFavoriteLocation
    }
}