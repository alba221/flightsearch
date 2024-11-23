package com.learningcompose.flightsearch.data

import android.content.Context

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppDataContainer(context: Context) : AppContainer {
    override val flightSearchRepository: FlightSearchRepository by lazy {
        FlightSearchRepositoryImpl(FlightSearchDatabase.getDatabase(context).flightSearchDao())
    }
}