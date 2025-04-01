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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
    ): Flow<Result<Weather?>> = flow {
        emit(Result.Loading)
        if (refresh) {
            val mapper = WeatherMapperRemote()
            when (val response = remoteDataSource.getWeather(location)) {
                is Result.Success -> {
                    if (response.data != null) {
                        emit(Result.Success(mapper.transformToDomain(response.data)))
                    } else {
                        emit(Result.Success(null))
                    }
                }
                is Result.Error -> {
                    emit(Result.Error(response.exception))
                }
                else -> emit(Result.Loading)
            }
        } else {
            val mapper = WeatherMapperLocal()
            val forecast = localDataSource.getWeather()
            if (forecast != null) {
                emit(Result.Success(mapper.transformToDomain(forecast)))
            } else {
                emit(Result.Success(null))
            }
        }
    }.flowOn(ioDispatcher)

    // override suspend fun getForecastWeather(cityId: Int, refresh: Boolean): Result<List<WeatherForecast>?> = withContext(ioDispatcher) {
    override suspend fun getForecastWeather(
        location: LocationModel,
        refresh: Boolean,
    ): Flow<Result<List<WeatherForecast>?>> = flow {
        emit(Result.Loading)
        if (refresh) {
            val mapper = WeatherForecastMapperRemote()
            when (val response = remoteDataSource.getWeatherForecast(location)) {
                is Result.Success -> {
                    if (response.data != null) {
                        emit(Result.Success(mapper.transformToDomain(response.data)))
                    } else {
                        emit(Result.Success(null))
                    }
                }
                is Result.Error -> {
                    emit(Result.Error(response.exception))
                }
                else -> emit(Result.Loading)
            }
        } else {
            val mapper = WeatherForecastMapperLocal()
            val forecast = localDataSource.getForecastWeather()
            if (forecast != null) {
                emit(Result.Success(mapper.transformToDomain(forecast)))
            } else {
                emit(Result.Success(null))
            }
        }
    }.flowOn(ioDispatcher)

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

    override suspend fun getSearchWeather(location: String): Flow<Result<Weather?>> = flow {
        emit(Result.Loading)
        val mapper = WeatherMapperRemote()
        when (val response = remoteDataSource.getSearchWeather(location)) {
            is Result.Success -> {
                if (response.data != null) {
                    emit(Result.Success(mapper.transformToDomain(response.data)))
                } else {
                    emit(Result.Success(null))
                }
            }
            is Result.Error -> {
                emit(Result.Error(response.exception))
            }
            else -> emit(Result.Loading)
        }
    }.flowOn(ioDispatcher)

    override suspend fun deleteWeatherData() =
        withContext(ioDispatcher) {
            localDataSource.deleteWeather()
        }

    override suspend fun deleteForecastData() =
        withContext(ioDispatcher) {
            localDataSource.deleteForecastWeather()
        }

    override suspend fun storeFavoriteLocationData(favoriteLocation: FavoriteLocation): Flow<Result<List<Long>>> = flow {
        emit(Result.Loading)
        val mapper = FavoriteLocationMapperLocal()
        val storeFavoriteLocationResult = localDataSource.saveFavoriteLocation(mapper.transformToDto(favoriteLocation))
        emit(Result.Success(storeFavoriteLocationResult))
    }.flowOn(ioDispatcher)

    override suspend fun getFavoriteLocations(): Flow<Result<List<FavoriteLocation>?>> =
        localDataSource.getFavoriteLocations().map { favoriteLocations ->
            if (favoriteLocations != null) {
                Result.Success(FavoriteLocationListMapperLocal().transformToDomain(favoriteLocations))
            } else {
                Result.Success(null)
            }
        }.flowOn(ioDispatcher)

    override suspend fun deleteFavoriteLocation(name: String): Flow<Result<Int>> = flow {
        emit(Result.Loading)
        val deleteFavoriteLocationResult = localDataSource.deleteFavoriteLocation(name)
        emit(Result.Success(deleteFavoriteLocationResult))
    }.flowOn(ioDispatcher)
}
