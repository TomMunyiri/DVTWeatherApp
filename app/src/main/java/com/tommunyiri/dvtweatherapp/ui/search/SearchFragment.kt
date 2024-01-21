package com.tommunyiri.dvtweatherapp.ui.search

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.google.android.material.snackbar.Snackbar
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.data.model.SearchResult
import com.tommunyiri.dvtweatherapp.data.model.Weather
import com.tommunyiri.dvtweatherapp.databinding.FragmentSearchBinding
import com.tommunyiri.dvtweatherapp.databinding.FragmentSearchDetailBinding
import com.tommunyiri.dvtweatherapp.ui.BaseBottomSheetDialog
import com.tommunyiri.dvtweatherapp.ui.BaseFragment
import com.tommunyiri.dvtweatherapp.ui.MainActivity
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment(), SearchResultAdapter.OnItemClickedListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchDetailBinding: FragmentSearchDetailBinding
    private lateinit var selectedCity: FavoriteLocation
    private val bottomSheetDialog by lazy {
        BaseBottomSheetDialog(
            requireActivity(),
            R.style.AppBottomSheetDialogTheme
        )
    }
    private val viewModel by viewModels<SearchFragmentViewModel> { viewModelFactoryProvider }
    private val searchResultAdapter by lazy { SearchResultAdapter(this) }
    private val connection = ConnectionHandler()
    private lateinit var searchBoxView: SearchBoxViewAppCompat
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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        searchDetailBinding = FragmentSearchDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBoxView = SearchBoxViewAppCompat(binding.searchView)
        searchBoxView.searchView.isIconified = false

        val statsView = StatsTextView(binding.stats)
        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        searchBoxView.onQuerySubmitted = {
            binding.zeroHits.visibility = View.GONE
            if (!it.isNullOrEmpty()) {
                viewModel.getSearchWeather(it)
            }
        }

        val recyclerView = binding.locationSearchRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = searchResultAdapter

        searchDetailBinding.fabClose.setOnClickListener {
            if (bottomSheetDialog.isShowing)
                bottomSheetDialog.dismiss()
        }

        searchDetailBinding.ivFavorite.setOnClickListener {
            viewModel.saveFavoriteLocation(selectedCity)
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        with(viewModel) {

            locations.observe(viewLifecycleOwner) { hits ->
                searchResultAdapter.submitList(hits)
                binding.zeroHits.isVisible = hits.size == 0
            }

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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).apply {
            showToolBar()
            resetTransparentStatusBar()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.disconnect()
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        searchBoxView.setText(searchResult.name)
        viewModel.getSearchWeather(searchResult.name)
    }
}