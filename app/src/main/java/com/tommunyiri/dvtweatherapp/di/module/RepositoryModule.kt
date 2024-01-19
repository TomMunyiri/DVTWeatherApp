package com.tommunyiri.dvtweatherapp.di.module

import com.tommunyiri.dvtweatherapp.data.source.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.data.source.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository
}