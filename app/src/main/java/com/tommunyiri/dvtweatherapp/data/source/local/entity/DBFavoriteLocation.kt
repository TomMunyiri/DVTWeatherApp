package com.tommunyiri.dvtweatherapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@Entity(tableName = "favorite_locations_table")
data class DBFavoriteLocation(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String
)
