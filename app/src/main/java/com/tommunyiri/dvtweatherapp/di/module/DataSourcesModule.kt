package com.tommunyiri.dvtweatherapp.di.module

import com.tommunyiri.dvtweatherapp.data.sources.local.database.WeatherLocalDataSource
import com.tommunyiri.dvtweatherapp.data.sources.local.database.WeatherLocalDataSourceImpl
import com.tommunyiri.dvtweatherapp.data.sources.remote.WeatherRemoteDataSource
import com.tommunyiri.dvtweatherapp.data.sources.remote.WeatherRemoteDataSourceImpl
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
abstract class DataSourcesModule {

    @Binds
    abstract fun bindLocalDataSource(localDataSourceImpl: WeatherLocalDataSourceImpl): WeatherLocalDataSource

    @Binds
    abstract fun bindRemoteDataSource(remoteDataSourceImpl: WeatherRemoteDataSourceImpl): WeatherRemoteDataSource
}