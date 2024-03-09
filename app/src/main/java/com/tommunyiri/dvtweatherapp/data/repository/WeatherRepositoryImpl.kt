package com.tommunyiri.dvtweatherapp.data.repository

import com.tommunyiri.dvtweatherapp.core.di.scope.IoDispatcher
import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.data.mappers.FavoriteLocationListMapperLocal
import com.tommunyiri.dvtweatherapp.data.mappers.FavoriteLocationMapperLocal
import com.tommunyiri.dvtweatherapp.data.mappers.WeatherForecastMapperLocal
import com.tommunyiri.dvtweatherapp.data.mappers.WeatherForecastMapperRemote
import com.tommunyiri.dvtweatherapp.data.mappers.WeatherMapperLocal
import com.tommunyiri.dvtweatherapp.data.mappers.WeatherMapperRemote
import com.tommunyiri.dvtweatherapp.data.sources.local.database.WeatherLocalDataSource
import com.tommunyiri.dvtweatherapp.data.sources.remote.WeatherRemoteDataSource
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: WeatherRemoteDataSource,
        private val localDataSource: WeatherLocalDataSource,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : WeatherRepository {
        override suspend fun getWeather(
            location: LocationModel,
            refresh: Boolean,
        ): Result<Weather> =
            withContext(ioDispatcher) {
                if (refresh) {
                    val mapper = WeatherMapperRemote()
                    when (val response = remoteDataSource.getWeather(location)) {
                        is Result.Success -> {
                            if (response.data != null) {
                                Result.Success(mapper.transformToDomain(response.data))
                            } else {
                                Result.Success(null)
                            }
                        }

                        is Result.Error -> {
                            Result.Error(response.exception)
                        }

                        else -> Result.Loading
                    }
                } else {
                    val mapper = WeatherMapperLocal()
                    val forecast = localDataSource.getWeather()
                    if (forecast != null) {
                        Result.Success(mapper.transformToDomain(forecast))
                    } else {
                        Result.Success(null)
                    }
                }
            }

        // override suspend fun getForecastWeather(cityId: Int, refresh: Boolean): Result<List<WeatherForecast>?> = withContext(ioDispatcher) {
        override suspend fun getForecastWeather(
            location: LocationModel,
            refresh: Boolean,
        ): Result<List<WeatherForecast>?> =
            withContext(ioDispatcher) {
                if (refresh) {
                    val mapper = WeatherForecastMapperRemote()
                    when (val response = remoteDataSource.getWeatherForecast(location)) {
                        is Result.Success -> {
                            if (response.data != null) {
                                Result.Success(mapper.transformToDomain(response.data))
                            } else {
                                Result.Success(null)
                            }
                        }

                        is Result.Error -> {
                            Result.Error(response.exception)
                        }

                        else -> Result.Loading
                    }
                } else {
                    val mapper = WeatherForecastMapperLocal()
                    val forecast = localDataSource.getForecastWeather()
                    if (forecast != null) {
                        Result.Success(mapper.transformToDomain(forecast))
                    } else {
                        Result.Success(null)
                    }
                }
            }

        override suspend fun storeWeatherData(weather: Weather) =
            withContext(ioDispatcher) {
                val mapper = WeatherMapperLocal()
                localDataSource.saveWeather(mapper.transformToDto(weather))
            }

        override suspend fun storeForecastData(forecasts: List<WeatherForecast>) =
            withContext(ioDispatcher) {
                val mapper = WeatherForecastMapperLocal()
                mapper.transformToDto(forecasts).let { listOfDbForecast ->
                    listOfDbForecast.forEach {
                        localDataSource.saveForecastWeather(it)
                    }
                }
            }

        override suspend fun getSearchWeather(location: String): Result<Weather?> =
            withContext(ioDispatcher) {
                val mapper = WeatherMapperRemote()
                return@withContext when (val response = remoteDataSource.getSearchWeather(location)) {
                    is Result.Success -> {
                        if (response.data != null) {
                            Result.Success(mapper.transformToDomain(response.data))
                        } else {
                            Result.Success(null)
                        }
                    }

                    is Result.Error -> {
                        Result.Error(response.exception)
                    }

                    else -> {
                        Result.Loading
                    }
                }
            }

        override suspend fun deleteWeatherData() =
            withContext(ioDispatcher) {
                localDataSource.deleteWeather()
            }

        override suspend fun deleteForecastData() {
            localDataSource.deleteForecastWeather()
        }

        override suspend fun storeFavoriteLocationData(favoriteLocation: FavoriteLocation): Result<List<Long>> =
            withContext(ioDispatcher) {
                val mapper = FavoriteLocationMapperLocal()
                val storeFavoriteLocationResult =
                    localDataSource.saveFavoriteLocation(mapper.transformToDto(favoriteLocation))
                Result.Success(storeFavoriteLocationResult)
            }

        override suspend fun getFavoriteLocations(): Result<List<FavoriteLocation>?> =
            withContext(ioDispatcher) {
                val mapper = FavoriteLocationListMapperLocal()
                val favoriteLocations = localDataSource.getFavoriteLocations()
                if (favoriteLocations != null) {
                    Result.Success(mapper.transformToDomain(favoriteLocations))
                } else {
                    Result.Success(null)
                }
            }

        override suspend fun deleteFavoriteLocation(name: String): Result<Int> =
            withContext(ioDispatcher) {
                val deleteFavoriteLocationResult = localDataSource.deleteFavoriteLocation(name)
                Result.Success(deleteFavoriteLocationResult)
            }
    }
