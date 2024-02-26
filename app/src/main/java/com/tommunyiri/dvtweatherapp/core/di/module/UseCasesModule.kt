package com.tommunyiri.dvtweatherapp.core.di.module

import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.domain.usecases.DeleteFavoriteLocationUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.DeleteWeatherDataUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.DeleteWeatherForecastUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.GetFavoriteLocationsUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.GetSearchWeatherUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.GetWeatherForecastUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.GetWeatherUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.SaveFavoriteLocationUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.StoreWeatherDataUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.StoreWeatherForecastUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.WeatherUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@InstallIn(SingletonComponent::class)
@Module
class UseCasesModule {
    @Provides
    @Singleton
    fun provideSharedPreferencesUseCase(): GetSharedPreferencesUseCase =
        GetSharedPreferencesUseCase()

    @Provides
    @Singleton
    fun provideWeatherUseCases(weatherRepository: WeatherRepository): WeatherUseCases {
        return WeatherUseCases(
            getWeather = GetWeatherUseCase(weatherRepository),
            getWeatherForecast = GetWeatherForecastUseCase(weatherRepository),
            deleteWeatherData = DeleteWeatherDataUseCase(weatherRepository),
            deleteWeatherForecast = DeleteWeatherForecastUseCase(weatherRepository),
            getSearchWeather = GetSearchWeatherUseCase(weatherRepository),
            storeWeatherForecast = StoreWeatherForecastUseCase(weatherRepository),
            storeWeatherData = StoreWeatherDataUseCase(weatherRepository),
            saveFavoriteLocation = SaveFavoriteLocationUseCase(weatherRepository),
            deleteFavoriteLocation = DeleteFavoriteLocationUseCase(weatherRepository),
            getFavoriteLocations = GetFavoriteLocationsUseCase(weatherRepository)
        )
    }
}