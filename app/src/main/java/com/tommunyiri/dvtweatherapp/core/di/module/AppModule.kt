package com.tommunyiri.dvtweatherapp.core.di.module

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.tommunyiri.dvtweatherapp.BuildConfig
import com.tommunyiri.dvtweatherapp.data.repository.LocationRepository
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(context: Context): SharedPreferenceHelper {
        return SharedPreferenceHelper.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(context: Context): LocationRepository {
        return LocationRepository(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        client: Lazy<OkHttpClient>,
        converterFactory: GsonConverterFactory,
    ): Retrofit {
        val retrofitBuilder =
            Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client.get())
                .addConverterFactory(converterFactory)

        val okHttpClientBuilder =
            OkHttpClient.Builder()
                .addInterceptor { chain ->

                    val original = chain.request()
                    val originalHttpUrl = original.url

                    val url =
                        originalHttpUrl.newBuilder()
                            .addQueryParameter("appid", BuildConfig.API_KEY)
                            .build()

                    Timber.d("Started making network call")

                    val requestBuilder =
                        original.newBuilder()
                            .url(url)

                    val request = requestBuilder.build()
                    return@addInterceptor chain.proceed(request)
                }
                .readTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            // set your desired log level
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            // okHttpClientBuilder.addInterceptor(ChuckInterceptor(context))
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return retrofitBuilder.client(okHttpClientBuilder.build()).build()
    }
}
