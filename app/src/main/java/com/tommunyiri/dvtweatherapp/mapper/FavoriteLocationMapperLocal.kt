package com.tommunyiri.dvtweatherapp.mapper

import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.model.Weather
import com.tommunyiri.dvtweatherapp.data.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeather


/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class FavoriteLocationMapperLocal : BaseMapper<DBFavoriteLocation, FavoriteLocation> {

    override fun transformToDomain(type: DBFavoriteLocation): FavoriteLocation =
        FavoriteLocation(type.name)


    override fun transformToDto(type: FavoriteLocation): DBFavoriteLocation =
        DBFavoriteLocation(type.name)
}