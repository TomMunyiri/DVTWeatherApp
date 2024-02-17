package com.tommunyiri.dvtweatherapp.data.mapper

import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.local.entity.DBFavoriteLocation


/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class FavoriteLocationMapperLocal : BaseMapper<DBFavoriteLocation, FavoriteLocation> {

    override fun transformToDomain(type: DBFavoriteLocation): FavoriteLocation =
        FavoriteLocation(type.name, type.lat, type.lon, type.country)


    override fun transformToDto(type: FavoriteLocation): DBFavoriteLocation =
        DBFavoriteLocation(type.name, type.lat, type.lon, type.country)
}