package com.tommunyiri.dvtweatherapp.ui.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.databinding.FragmentFavoritesBinding
import com.tommunyiri.dvtweatherapp.ui.BaseFragment
import com.tommunyiri.dvtweatherapp.ui.MainActivity
import com.tommunyiri.dvtweatherapp.ui.home.HomeFragmentViewModel
import com.tommunyiri.dvtweatherapp.ui.home.WeatherForecastAdapter
import com.tommunyiri.dvtweatherapp.ui.search.SearchResultAdapter
import com.tommunyiri.dvtweatherapp.utils.convertCelsiusToFahrenheit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(), FavoriteLocationsAdapter.OnItemClickedListener {
    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel by viewModels<FavoriteFragmentViewModel> { viewModelFactoryProvider }
    private val favoriteLocationsAdapter by lazy { FavoriteLocationsAdapter(this) }
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onSearchResultClicked(dbFavoriteLocation: FavoriteLocation) {
        TODO("Not yet implemented")
    }
}