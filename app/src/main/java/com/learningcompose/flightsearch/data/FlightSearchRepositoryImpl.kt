package com.learningcompose.flightsearch.data

import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [FlightSearchRepository] that retrieves Flight Search data from underlying data source (Room DB)
 * and inserts favorite flights into Room DB
 */
class FlightSearchRepositoryImpl(private val flightSearchDao: FlightSearchDao) : FlightSearchRepository {
    override suspend fun insertFavorite(favorite: Favorite) = flightSearchDao.insertFavorite(favorite)
    override suspend fun deleteFavorite(departureCode: String, destinationCode: String) =
        flightSearchDao.deleteFavorite(departureCode, destinationCode)
    override fun getAllAirports(): Flow<List<Airport>> = flightSearchDao.getAllAirports()
    override fun getAirportByIAtaCode(iAtaCode: String): Flow<Airport> =
        flightSearchDao.getAirportByIAtaCode(iAtaCode)
    override fun searchAirports(
        airportName: String,
        iAtaCode: String
    ): Flow<List<Airport>> = flightSearchDao.searchAirports(airportName, iAtaCode)
    override fun getAllFavorite(): Flow<List<Favorite>> = flightSearchDao.getAllFavorites()
}