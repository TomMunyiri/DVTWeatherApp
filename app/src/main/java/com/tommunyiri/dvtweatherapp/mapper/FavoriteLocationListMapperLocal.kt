package com.tommunyiri.dvtweatherapp.mapper

import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBFavoriteLocation


/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class FavoriteLocationListMapperLocal :
    BaseMapper<List<DBFavoriteLocation>, List<FavoriteLocation>> {
    override fun transformToDomain(type: List<DBFavoriteLocation>): List<FavoriteLocation> {
        return type.map { dbFavoriteLocation ->
            FavoriteLocation(dbFavoriteLocation.name)
        }
    }

    override fun transformToDto(type: List<FavoriteLocation>): List<DBFavoriteLocation> {
        return type.map { favoriteLocation ->
            DBFavoriteLocation(favoriteLocation.name)
        }
    }
}