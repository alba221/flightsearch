package com.learningcompose.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FlightDetail(
    val airportDepart: Airport,
    val airportArrive: Airport,
    val isFavorite: Boolean
)

data class AirportAutocomplete(
    val iAtaCode: String,
    val airportName: String
)

/** Represents UI state */
data class HomeUiState(
    val flights: List<FlightDetail> = emptyList(),
    val favoriteRoutes: List<FlightDetail> = emptyList(),
    val autocompleteList: List<AirportAutocomplete> = emptyList(),
    val selectedDepartAirport: Airport? = null,
    val errorMessage: String? = null
)

/**
 * Viewmodel containing methods for access Room DB data trough [FlightSearchRepository]
 * and Preferences DataStore trough [FlightSearchPreferencesRepository]
 */
class FlightSearchViewModel(
    val flightSearchRepository: FlightSearchRepository,
    val flightSearchPreferencesRepository: FlightSearchPreferencesRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    /** Holds current UI state */
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    var searchText by mutableStateOf("")
        private set

    init {
        initialize()
    }

    fun initialize() {
        viewModelScope.launch {
            /** Search text stored in Preferences DataStore */
            flightSearchPreferencesRepository.getSearchText.collect { text ->
                if (text.isNotEmpty()) {
                    updateSearchText(text)
                }
            }
        }
        getFavorites()
    }

    fun updateSearchText(newSearchText: String) {
        searchText = newSearchText
        /** Save searchText to DataStore and perform search on database */
        viewModelScope.launch {
            withContext(defaultDispatcher) {
                flightSearchPreferencesRepository.saveSearchText(newSearchText)
            }
            flightSearchRepository
                .searchAirports(airportName = "%$searchText%", iAtaCode = searchText.uppercase())
                .catch { ex -> _homeUiState.value = HomeUiState(errorMessage = ex.message) }
                .collect { airports ->
                    /** Autocomplete list based on user text input in Search bar */
                    val autocompleteList = mutableListOf<AirportAutocomplete>()
                    for(airport in airports) {
                        autocompleteList.add(
                            AirportAutocomplete(iAtaCode = airport.iAtaCode, airportName = airport.name)
                        )
                    }
                    _homeUiState.update {
                        it.copy(autocompleteList = autocompleteList)
                    }
                }
        }
    }

    fun loadFlights(iAtaCode: String) {
        setSelectedAirport(iAtaCode)
        viewModelScope.launch {
            combineFlows(
                flightSearchRepository.getAllAirports(),
                flightSearchRepository.getAllFavorite()
            ).catch { ex -> _homeUiState.value = HomeUiState(errorMessage = ex.message) }
                .collect { (airports, favorites) ->
                    val airportDepart = _homeUiState.value.selectedDepartAirport
                    airportDepart?.let {
                        /** All possible flights for selected airport including favorites */
                        val flights = mutableListOf<FlightDetail>()
                        airports.forEach { airport ->
                            /** Depart airport has flights to every other airport except for itself */
                            if (airportDepart.iAtaCode != airport.iAtaCode) {
                                val favoriteFlight = favorites.find {
                                    it.departureCode == airportDepart.iAtaCode &&
                                            it.destinationCode == airport.iAtaCode
                                }
                                flights.add(FlightDetail(
                                    airportDepart = airportDepart,
                                    airportArrive = airport,
                                    isFavorite = favoriteFlight != null
                                ))
                            }
                        }
                        _homeUiState.update {
                            it.copy(flights = flights)
                        }
                    }
                }
        }
    }

    fun getFavorites() = viewModelScope.launch {
        combineFlows(
            flightSearchRepository.getAllAirports(),
            flightSearchRepository.getAllFavorite()
        ).catch { ex -> _homeUiState.value = HomeUiState(errorMessage = ex.message) }
            .collect { (airports, favorites) ->
                /** All favorite flights */
                val favoriteFlights = mutableListOf<FlightDetail>()
                for (favorite in favorites) {
                    val airportDepart = airports.find { it.iAtaCode == favorite.departureCode } ?: defaultAirport
                    val airportArrive = airports.find { it.iAtaCode == favorite.destinationCode } ?: defaultAirport
                    favoriteFlights.add(FlightDetail(
                        airportDepart = airportDepart,
                        airportArrive = airportArrive,
                        isFavorite = true
                    ))
                }
                _homeUiState.update {
                    it.copy(favoriteRoutes = favoriteFlights)
                }
            }
    }

    fun insertFavorite(favoriteRoute: Favorite) = viewModelScope.launch {
        flightSearchRepository.insertFavorite(favoriteRoute)
    }

    fun deleteFavorite(favorite: Favorite) = viewModelScope.launch {
        flightSearchRepository.deleteFavorite(favorite.departureCode, favorite.destinationCode)
    }

    fun removeSelectedAirport() {
        _homeUiState.update {
            it.copy(selectedDepartAirport = null)
        }
    }

    /** Set Depart airport based on selected IATA code value from autocomplete list */
    fun setSelectedAirport(iAtaCode: String) = viewModelScope.launch {
        flightSearchRepository.getAirportByIAtaCode(iAtaCode).collect { airport ->
            _homeUiState.update {
                it.copy(selectedDepartAirport = airport)
            }
        }
    }

    /** On click function when when all possible flights are shown */
    fun onClickFavorite(flightDetail: FlightDetail) {
        if (flightDetail.isFavorite) {
            deleteFavorite(flightDetail.toFavorite())
        } else {
            insertFavorite(flightDetail.toFavorite())
        }
    }

    /** On click function when only favorite routes are shown */
    fun onClickFavoriteList(flightDetail: FlightDetail) {
        deleteFavorite(flightDetail.toFavorite())
    }

    fun getSearchText() = flightSearchPreferencesRepository.getSearchText

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

fun <T1, T2> combineFlows(flow1: Flow<T1>, flow2: Flow<T2>): Flow<Pair<T1, T2>> =
    flow1.combine(flow2) { it1, it2 -> it1 to it2 }

fun FlightDetail.toFavorite() : Favorite = Favorite(
    id = 0,
    departureCode = airportDepart.iAtaCode,
    destinationCode = airportArrive.iAtaCode
)

val defaultAirport = Airport(-1, "", "", 0)