package com.learningcompose.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.learningcompose.flightsearch.data.AppContainer
import com.learningcompose.flightsearch.data.AppDataContainer
import com.learningcompose.flightsearch.data.FlightSearchPreferencesRepository

private const val FLIGHT_SEARCH_PREF_STORE = "flight_pref_store"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = FLIGHT_SEARCH_PREF_STORE
)

/**
 * Custom app entry point for manual dependency injection
 */
class FlightSearchApplication : Application() {

    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    /** FlightSearchPreferencesRepository instance used by the rest of classes to access
     * Preferences DataStore */
    lateinit var flightSearchPreferencesRepository: FlightSearchPreferencesRepository

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)

        flightSearchPreferencesRepository = FlightSearchPreferencesRepository(dataStore)
    }
}