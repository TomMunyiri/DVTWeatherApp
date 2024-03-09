package com.tommunyiri.dvtweatherapp.core.di.module

import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.domain.usecases.favorites.DeleteFavoriteLocationUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.favorites.GetFavoriteLocationsUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.favorites.SaveFavoriteLocationUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.preferences.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.DeleteWeatherDataUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.DeleteWeatherForecastUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.GetSearchWeatherUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.GetWeatherForecastUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.GetWeatherUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.StoreWeatherDataUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.StoreWeatherForecastUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases
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
    fun provideSharedPreferencesUseCase(): GetSharedPreferencesUseCase = GetSharedPreferencesUseCase()

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
            getFavoriteLocations = GetFavoriteLocationsUseCase(weatherRepository),
        )
    }
}
