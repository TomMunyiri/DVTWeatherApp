package com.tommunyiri.dvtweatherapp.data.source.repository

import com.tommunyiri.dvtweatherapp.MainCoroutineRule
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.data.sources.local.database.WeatherLocalDataSource
import com.tommunyiri.dvtweatherapp.data.sources.remote.WeatherRemoteDataSource
import com.tommunyiri.dvtweatherapp.data.repository.WeatherRepositoryImpl
import com.tommunyiri.dvtweatherapp.dummyLocation
import com.tommunyiri.dvtweatherapp.fakeDbWeatherEntity
import com.tommunyiri.dvtweatherapp.fakeDbWeatherForecast
import com.tommunyiri.dvtweatherapp.fakeLocationModel
import com.tommunyiri.dvtweatherapp.fakeNetworkWeather
import com.tommunyiri.dvtweatherapp.fakeNetworkWeatherForecast
import com.tommunyiri.dvtweatherapp.invalidDataException
import com.tommunyiri.dvtweatherapp.queryLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import com.tommunyiri.dvtweatherapp.core.utils.Result
import org.hamcrest.core.Is.`is`

/**
 * Created by Tom Munyiri on 22/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class WeatherRepositoryTest {
    //region helper fields
    @Mock
    private lateinit var remoteDataSource: WeatherRemoteDataSource

    @Mock
    private lateinit var localDataSource: WeatherLocalDataSource
    //endregion helper fields

    private lateinit var systemUnderTest: WeatherRepositoryImpl

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        systemUnderTest = WeatherRepositoryImpl(remoteDataSource, localDataSource, Dispatchers.Main)
    }

    @Test
    fun `assert that getWeather with refresh as true fetches successfully from the remote source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(remoteDataSource.getWeather(dummyLocation)).thenReturn(
                Result.Success(fakeNetworkWeather)
            )

            val response = systemUnderTest.getWeather(dummyLocation, true)

            Mockito.verify(remoteDataSource, Mockito.times(1)).getWeather(dummyLocation)
            Mockito.verifyNoMoreInteractions(localDataSource)

            when (response) {
                is Result.Success -> {
                    val weather = response.data
                    Assert.assertThat(
                        weather as Weather,
                        CoreMatchers.`is`(CoreMatchers.notNullValue())
                    )
                    Assert.assertThat(weather.name, `is`(fakeNetworkWeather.name))
                    Assert.assertThat(weather.cityId, `is`(fakeNetworkWeather.cityId))
                    Assert.assertThat(weather.wind, `is`(fakeNetworkWeather.wind))
                    Assert.assertThat(
                        weather.networkWeatherCondition,
                        `is`(fakeNetworkWeather.networkWeatherCondition)
                    )
                    Assert.assertThat(
                        weather.networkWeatherDescription,
                        `is`(fakeNetworkWeather.networkWeatherDescriptions)
                    )
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }

    @Test
    fun `assert that getWeather with refresh as false fetches successfully from the local source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(localDataSource.getWeather()).thenReturn(
                fakeDbWeatherEntity
            )

            val response = systemUnderTest.getWeather(dummyLocation, false)

            Mockito.verify(localDataSource, Mockito.times(1)).getWeather()
            Mockito.verifyNoMoreInteractions(remoteDataSource)

            when (response) {
                is Result.Success -> {
                    val weather = response.data
                    Assert.assertThat(
                        weather as Weather,
                        CoreMatchers.`is`(CoreMatchers.notNullValue())
                    )
                    Assert.assertThat(weather.name, `is`(fakeDbWeatherEntity.cityName))
                    Assert.assertThat(weather.cityId, `is`(fakeDbWeatherEntity.cityId))
                    Assert.assertThat(weather.wind, `is`(fakeDbWeatherEntity.wind))
                    Assert.assertThat(
                        weather.networkWeatherCondition,
                        `is`(fakeDbWeatherEntity.networkWeatherCondition)
                    )
                    Assert.assertThat(
                        weather.networkWeatherDescription,
                        `is`(fakeDbWeatherEntity.networkWeatherDescription)
                    )
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }

    @Test
    fun `assert that getWeather with refresh as true fetches from the remote source but returns an Error`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(remoteDataSource.getWeather(dummyLocation)).thenReturn(
                Result.Error(
                    invalidDataException
                )
            )

            val response = systemUnderTest.getWeather(dummyLocation, true)

            Mockito.verify(remoteDataSource, Mockito.times(1)).getWeather(dummyLocation)
            Mockito.verifyNoMoreInteractions(localDataSource)

            when (response) {
                is Result.Error -> {
                    Assert.assertThat(response.exception, `is`(invalidDataException))
                }

                Result.Loading -> TODO()
                is Result.Success -> TODO()
            }
        }

    @Test
    fun `assert that getWeather with refresh as false fetches null data from the local source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(localDataSource.getWeather()).thenReturn(
                null
            )

            val response = systemUnderTest.getWeather(dummyLocation, false)

            Mockito.verify(localDataSource, Mockito.times(1)).getWeather()
            Mockito.verifyNoMoreInteractions(remoteDataSource)

            when (response) {
                is Result.Success -> {
                    Assert.assertThat(response.data, CoreMatchers.`is`(CoreMatchers.nullValue()))
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }

    @Test
    fun `assert that getForecastWeather with refresh as true fetches successfully from the remote source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(remoteDataSource.getWeatherForecast(fakeLocationModel)).thenReturn(
                Result.Success(listOf(fakeNetworkWeatherForecast))
            )

            val response = systemUnderTest.getForecastWeather(fakeLocationModel, true)

            Mockito.verify(remoteDataSource, Mockito.times(1)).getWeatherForecast(fakeLocationModel)
            Mockito.verifyNoMoreInteractions(localDataSource)

            when (response) {
                is Result.Success -> {
                    val forecast = response.data?.first()
                    Assert.assertThat(
                        forecast as WeatherForecast,
                        CoreMatchers.`is`(CoreMatchers.notNullValue())
                    )
                    Assert.assertThat(forecast.date, `is`(fakeNetworkWeatherForecast.date))
                    Assert.assertThat(forecast.uID, `is`(fakeNetworkWeatherForecast.id))
                    Assert.assertThat(forecast.wind, `is`(fakeNetworkWeatherForecast.wind))
                    Assert.assertThat(
                        forecast.networkWeatherCondition,
                        `is`(fakeNetworkWeatherForecast.networkWeatherCondition)
                    )
                    Assert.assertThat(
                        forecast.networkWeatherDescription,
                        `is`(fakeNetworkWeatherForecast.networkWeatherDescription)
                    )
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }

    @Test
    fun `assert that getForecastWeather with refresh as false fetches successfully from the local source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(localDataSource.getForecastWeather()).thenReturn(
                listOf(fakeDbWeatherForecast)
            )

            val response = systemUnderTest.getForecastWeather(fakeLocationModel, false)

            Mockito.verify(localDataSource, Mockito.times(1)).getForecastWeather()
            Mockito.verifyNoMoreInteractions(remoteDataSource)

            when (response) {
                is Result.Success -> {
                    val forecast = response.data?.first()
                    Assert.assertThat(
                        forecast as WeatherForecast,
                        CoreMatchers.`is`(CoreMatchers.notNullValue())
                    )
                    Assert.assertThat(forecast.date, `is`(fakeDbWeatherForecast.date))
                    Assert.assertThat(forecast.uID, `is`(fakeDbWeatherForecast.id))
                    Assert.assertThat(forecast.wind, `is`(fakeDbWeatherForecast.wind))
                    Assert.assertThat(
                        forecast.networkWeatherCondition,
                        `is`(fakeDbWeatherForecast.networkWeatherCondition)
                    )
                    Assert.assertThat(
                        forecast.networkWeatherDescription,
                        `is`(fakeDbWeatherForecast.networkWeatherDescriptions)
                    )
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }

    @Test
    fun `assert that getForecastWeather with refresh as false fetches null data from the local source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(localDataSource.getForecastWeather()).thenReturn(
                null
            )

            val response = systemUnderTest.getForecastWeather(fakeLocationModel, false)

            Mockito.verify(localDataSource, Mockito.times(1)).getForecastWeather()
            Mockito.verifyNoMoreInteractions(remoteDataSource)

            when (response) {
                is Result.Success -> {
                    Assert.assertThat(response.data, CoreMatchers.`is`(CoreMatchers.nullValue()))
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }

    @Test
    fun `assert that getSearchWeather fetches successfully from the remote source`() =
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(remoteDataSource.getSearchWeather(queryLocation))
                .thenReturn(Result.Success(fakeNetworkWeather))

            val response = systemUnderTest.getSearchWeather(queryLocation)

            Mockito.verify(remoteDataSource, Mockito.times(1)).getSearchWeather(queryLocation)

            when (response) {
                is Result.Success -> {
                    val weather = response.data
                    Assert.assertThat(
                        weather as Weather,
                        CoreMatchers.`is`(CoreMatchers.notNullValue())
                    )
                    Assert.assertThat(weather.name, `is`(fakeNetworkWeather.name))
                    Assert.assertThat(weather.cityId, `is`(fakeNetworkWeather.cityId))
                    Assert.assertThat(weather.wind, `is`(fakeNetworkWeather.wind))
                    Assert.assertThat(
                        weather.networkWeatherCondition,
                        `is`(fakeNetworkWeather.networkWeatherCondition)
                    )
                    Assert.assertThat(
                        weather.networkWeatherDescription,
                        `is`(fakeNetworkWeather.networkWeatherDescriptions)
                    )
                }

                is Result.Error -> TODO()
                Result.Loading -> TODO()
            }
        }
}
