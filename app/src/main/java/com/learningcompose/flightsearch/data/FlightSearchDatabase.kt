package com.learningcompose.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class FlightSearchDatabase : RoomDatabase() {

    abstract fun flightSearchDao(): FlightSearchDao

    companion object {
        @Volatile
        private var Instance: FlightSearchDatabase? = null

        fun getDatabase(context: Context): FlightSearchDatabase {
            /** if the Instance is not null, return it, otherwise create a new database instance */
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FlightSearchDatabase::class.java, "flight_database")
                    .createFromAsset("database/flight_search.db")
                    .build()
                    .also {
                        Instance = it
                    }
            }

        }
    }

}