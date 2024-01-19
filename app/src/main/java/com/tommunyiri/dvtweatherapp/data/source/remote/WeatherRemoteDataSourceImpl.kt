package com.tommunyiri.dvtweatherapp.data.source.remote

import com.tommunyiri.dvtweatherapp.data.model.LocationModel
import com.tommunyiri.dvtweatherapp.data.model.NetworkWeather
import com.tommunyiri.dvtweatherapp.data.model.NetworkWeatherForecast
import com.tommunyiri.dvtweatherapp.data.source.remote.retrofit.WeatherApiService
import com.tommunyiri.dvtweatherapp.di.scope.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.tommunyiri.dvtweatherapp.utils.Result

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherRemoteDataSourceImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val apiService: WeatherApiService
) : WeatherRemoteDataSource {
    override suspend fun getWeather(location: LocationModel): Result<NetworkWeather> =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = apiService.getCurrentWeather(
                    location.latitude, location.longitude
                )
                if (result.isSuccessful) {
                    val networkWeather = result.body()
                    Result.Success(networkWeather)
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }

    override suspend fun getWeatherForecast(cityId: Int): Result<List<NetworkWeatherForecast>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = apiService.getWeatherForecast(cityId)
                if (result.isSuccessful) {
                    val networkWeatherForecast = result.body()?.weathers
                    Result.Success(networkWeatherForecast)
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }

    override suspend fun getSearchWeather(query: String): Result<NetworkWeather> =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = apiService.getSpecificWeather(query)
                if (result.isSuccessful) {
                    val networkWeather = result.body()
                    Result.Success(networkWeather)
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }
}