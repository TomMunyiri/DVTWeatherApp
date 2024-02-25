package com.tommunyiri.dvtweatherapp.data.mappers


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface BaseMapper<E, D> {

    fun transformToDomain(type: E): D

    fun transformToDto(type: D): E
}