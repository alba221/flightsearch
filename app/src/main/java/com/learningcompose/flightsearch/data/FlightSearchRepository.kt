package com.learningcompose.flightsearch.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that retrieves Flight Search data [Airport] from underlying data source (Room DB).
 * Inserts favorite flights [Favorite] into Room DB
 */
interface FlightSearchRepository {
    suspend fun insertFavorite(favorite: Favorite)
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)
    fun getAllAirports(): Flow<List<Airport>>
    fun getAirportByIAtaCode(iAtaCode: String): Flow<Airport>
    fun searchAirports(airportName: String, iAtaCode: String): Flow<List<Airport>>
    fun getAllFavorite(): Flow<List<Favorite>>
}