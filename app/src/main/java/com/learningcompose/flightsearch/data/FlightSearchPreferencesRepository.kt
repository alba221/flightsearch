package com.learningcompose.flightsearch.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository that retrieves preferences from DataStore
 * */
class FlightSearchPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val SEARCH_TEXT = stringPreferencesKey("search_text")
    }

    suspend fun saveSearchText(text: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_TEXT] = text
        }
    }

    val getSearchText: Flow<String> = dataStore.data.map { preferences ->
        preferences[SEARCH_TEXT] ?: ""
    }
}

