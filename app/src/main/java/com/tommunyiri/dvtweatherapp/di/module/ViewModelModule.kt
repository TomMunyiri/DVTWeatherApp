package com.tommunyiri.dvtweatherapp.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tommunyiri.dvtweatherapp.ViewModelFactory
import com.tommunyiri.dvtweatherapp.di.key.ViewModelKey
import com.tommunyiri.dvtweatherapp.presentation.favorite.FavoriteFragmentViewModel
import com.tommunyiri.dvtweatherapp.presentation.home.HomeFragmentViewModel
import com.tommunyiri.dvtweatherapp.presentation.search.SearchFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(HomeFragmentViewModel::class)
    abstract fun bindHomeFragmentViewModel(viewModel: HomeFragmentViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(SearchFragmentViewModel::class)
    abstract fun bindSearchFragmentViewModel(viewModel: SearchFragmentViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(FavoriteFragmentViewModel::class)
    abstract fun bindFavoritesFragmentViewModel(viewModel: FavoriteFragmentViewModel): ViewModel
}