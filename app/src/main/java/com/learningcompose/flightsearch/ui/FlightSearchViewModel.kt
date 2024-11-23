package com.learningcompose.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learningcompose.flightsearch.FlightSearchApplication
import com.learningcompose.flightsearch.data.Airport
import com.learningcompose.flightsearch.data.Favorite
import com.learningcompose.flightsearch.data.FlightSearchPreferencesRepository
import com.learningcompose.flightsearch.data.FlightSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Viewmodel containing methods for access Room DB data trough [FlightSearchRepository]
 * and Preferences DataStore trough [FlightSearchPreferencesRepository]
 */

class FlightSearchViewModel(
    val flightSearchRepository: FlightSearchRepository,
    val flightSearchPreferencesRepository: FlightSearchPreferencesRepository,
) : ViewModel() {

    fun getSearchText() = flightSearchPreferencesRepository.getSearchText

    fun saveSearchPrefsText(text: String) {
        viewModelScope.launch {
            flightSearchPreferencesRepository.saveSearchText(text)
        }
    }

    fun getAllAirports(): Flow<List<Airport>> = flightSearchRepository.getAllAirports()

    fun getAirportByIAtaCode(iAtaCode: String): Flow<Airport> =
        flightSearchRepository.getAirportByIAtaCode(iAtaCode)

    fun searchAirports(airportName: String, iAtaCode: String): Flow<List<Airport>> =
        flightSearchRepository.searchAirports(airportName = airportName, iAtaCode = iAtaCode)

    fun getAllFavorites(): Flow<List<Favorite>> = flightSearchRepository.getAllFavorite()

    fun insertFavorite(favoriteRoute: Favorite) {
        viewModelScope.launch {
            flightSearchRepository.insertFavorite(favoriteRoute)
        }
    }

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch {
            flightSearchRepository.deleteFavorite(favorite.departureCode, favorite.destinationCode)
        }
    }

    /**
     * Factory for [FlightSearchViewModel] that takes [FlightSearchRepository]
     * and [FlightSearchPreferencesRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(
                    flightSearchRepository = application.container.flightSearchRepository,
                    flightSearchPreferencesRepository = application.flightSearchPreferencesRepository
                )
            }
        }
    }
}
