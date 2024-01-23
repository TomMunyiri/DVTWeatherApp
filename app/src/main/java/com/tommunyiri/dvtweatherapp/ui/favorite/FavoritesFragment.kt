package com.tommunyiri.dvtweatherapp.ui.favorite

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.tommunyiri.dvtweatherapp.ui.search.SearchFragmentViewModel
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.utils.makeGone
import com.tommunyiri.dvtweatherapp.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(), FavoriteLocationsAdapter.OnItemClickedListener {
    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel by viewModels<FavoriteFragmentViewModel> { viewModelFactoryProvider }
    private val searchViewModel by viewModels<SearchFragmentViewModel> { viewModelFactoryProvider }
    private lateinit var searchDetailBinding: FragmentSearchDetailBinding
    private var selectedCity: FavoriteLocation? = null
    private var selectedCityPosition by Delegates.notNull<Int>()
    private lateinit var favoriteLocationList: ArrayList<FavoriteLocation>
    private val favoriteLocationsAdapter by lazy { FavoriteLocationsAdapter(this) }
    private val bottomSheetDialog by lazy {
        BaseBottomSheetDialog(
            requireActivity(),
            R.style.AppBottomSheetDialogTheme
        )
    }
    private var isDelete = false

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
            isDelete = true
            selectedCity?.name?.let { it1 -> viewModel.deleteFavoriteLocation(it1) }
        }

        binding.fabGoogleMap.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList("favoriteLocations", favoriteLocationList)
            findNavController().navigate(
                R.id.action_favoritesFragment_to_favoriteLocationsMapFragment,
                bundle
            )
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
                    favoriteLocationList = list as ArrayList<FavoriteLocation>
                    if (list.isNotEmpty()) {
                        favoriteLocationsAdapter.submitList(list)
                        binding.apply {
                            rvFavoriteLocations.makeVisible()
                            tvZeroFavorites.makeGone()
                        }
                    } else {
                        binding.apply {
                            rvFavoriteLocations.makeGone()
                            tvZeroFavorites.makeVisible()
                        }
                    }
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

            deleteFavoriteLocationResult.observe(viewLifecycleOwner) { deleteFavoriteLocationResult ->
                deleteFavoriteLocationResult?.let {
                    if (it == 1) {
                        updateRecyclerView()
                        if (bottomSheetDialog.isShowing)
                            bottomSheetDialog.dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Deletion of favorite location failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            isFavoriteLocationDeletionLoading.observe(viewLifecycleOwner) { state ->
                binding.searchWeatherLoader.isVisible = state
            }

        }
    }

    private fun updateRecyclerView() {
        if (isDelete) {
            favoriteLocationList.remove(selectedCity)
            favoriteLocationsAdapter.notifyItemRemoved(selectedCityPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isDelete = false
        selectedCity = null
        searchViewModel.clearData()
        if (bottomSheetDialog.isShowing)
            bottomSheetDialog.dismiss()
    }

    private fun displayWeatherResult(result: Weather) {
        with(searchDetailBinding) {
            weatherCondition = result.networkWeatherDescription.first()
            location.text = result.name
            weather = result
            selectedCity = FavoriteLocation(
                result.name,
                result.networkWeatherCoordinates.lat,
                result.networkWeatherCoordinates.lon,
                result.networkSys.country
            )
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

    override fun onSearchResultClicked(dbFavoriteLocation: FavoriteLocation, position: Int) {
        searchViewModel.clearData()
        searchViewModel.getSearchWeather(dbFavoriteLocation.name)
        selectedCity = dbFavoriteLocation
        selectedCityPosition = position
    }
}