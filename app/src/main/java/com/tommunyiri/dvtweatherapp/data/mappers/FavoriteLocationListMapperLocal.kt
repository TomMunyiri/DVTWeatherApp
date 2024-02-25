package com.tommunyiri.dvtweatherapp.data.mappers

import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBFavoriteLocation


/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class FavoriteLocationListMapperLocal :
    BaseMapper<List<DBFavoriteLocation>, List<FavoriteLocation>> {
    override fun transformToDomain(type: List<DBFavoriteLocation>): List<FavoriteLocation> {
        return type.map { dbFavoriteLocation ->
            FavoriteLocation(
                dbFavoriteLocation.name, dbFavoriteLocation.lat,
                dbFavoriteLocation.lon, dbFavoriteLocation.country
            )
        }
    }

    override fun transformToDto(type: List<FavoriteLocation>): List<DBFavoriteLocation> {
        return type.map { favoriteLocation ->
            DBFavoriteLocation(
                favoriteLocation.name, favoriteLocation.lat,
                favoriteLocation.lon, favoriteLocation.country
            )
        }
    }
}