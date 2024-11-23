package com.learningcompose.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the FlightSearch database
 */
@Dao
interface FlightSearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iAtaCode")
    fun getAirportByIAtaCode(iAtaCode: String): Flow<Airport>

    @Query("SELECT * FROM airport WHERE name LIKE :airportName OR iata_code LIKE :iAtaCode ORDER BY passengers DESC")
    fun searchAirports(airportName: String, iAtaCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>
}