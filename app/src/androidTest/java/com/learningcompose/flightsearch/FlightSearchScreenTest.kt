package com.learningcompose.flightsearch

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learningcompose.flightsearch.data.Airport
import com.learningcompose.flightsearch.data.FlightSearchDatabase
import com.learningcompose.flightsearch.ui.FlightSearchApp
import com.learningcompose.flightsearch.ui.theme.FlightSearchTheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FlightSearchScreenTest {

    private lateinit var flightSearchDatabase: FlightSearchDatabase
    private lateinit var context: Context

    private val airportFCO = Airport(
        id = 12,
        name = "Leonardo da Vinci International Airport",
        iAtaCode = "FCO",
        passengers = 11662842
    )
    private val airportMUC = Airport(
        id = 28,
        name = "Munich International Airport",
        iAtaCode = "MUC",
        passengers = 47959885
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        flightSearchDatabase = Room.inMemoryDatabaseBuilder(context, FlightSearchDatabase::class.java)
            .build()

        composeTestRule.setContent {
            FlightSearchTheme {
                FlightSearchApp()
            }
        }
        /** Clear text in search bar before every test */
        composeTestRule
            .onNodeWithText(context.getString(R.string.enter_airport))
            .performTextClearance()
    }

    @After
    fun closeDb() {
        flightSearchDatabase.close()
    }

    /** When app is launched search bar is displayed */
    @Test
    fun homeScreen_verifyContentIsDisplayed() {
        composeTestRule.onNodeWithText(context.getString(R.string.enter_airport)).assertIsDisplayed()
    }

    /** When "FCO" is entered as search text then "Leonardo da Vinci International Airport"
     *  is displayed in Autocomplete list */
    @Test
    fun homeScreen_insertSearchText_verifyAutocompleteListIsDisplayed() {
        composeTestRule
            .onNodeWithText(context.getString(R.string.enter_airport))
            .performTextInput(airportFCO.iAtaCode)
        composeTestRule.onNodeWithTag("${airportFCO.iAtaCode}-${airportFCO.name}").assertIsDisplayed()
    }

    /** When "FCO" is entered as search text and "Leonardo da Vinci International Airport"
     *  is displayed in Autocomplete list
     *  then click on this item in the list will display search results (flights) */
    @Test
    fun homeScreen_clickOnAutocompleteList_verifyFlightSearchResultsAreDisplayed() {
        composeTestRule
            .onNodeWithText(context.getString(R.string.enter_airport))
            .performTextInput(airportFCO.iAtaCode)
        composeTestRule.onNodeWithTag("${airportFCO.iAtaCode}-${airportFCO.name}").performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.flights_from, airportFCO.iAtaCode)
        ).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("${R.string.arrive}-${airportMUC.iAtaCode}-${airportMUC.name}")
            .assertIsDisplayed()
    }

    /** Check if back button is working when search results (flights) are displayed */
    @Test
    fun homeScreen_clickOnAutocompleteList_verifyBackButtonIsWorking() {
        composeTestRule
            .onNodeWithText(context.getString(R.string.enter_airport))
            .performTextInput(airportFCO.iAtaCode)
        composeTestRule.onNodeWithTag("${airportFCO.iAtaCode}-${airportFCO.name}").performClick()
        composeTestRule.onNodeWithText(
            context.getString(R.string.flights_from, airportFCO.iAtaCode)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${airportFCO.iAtaCode}-${airportFCO.name}").assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.back_button)
        ).performClick()
        composeTestRule.onNodeWithTag("${airportFCO.iAtaCode}-${airportFCO.name}").assertIsDisplayed()
    }

    /** After the flight is set as favorite, check if the favorites flights are displayed correctly */
    @Test
    fun homeScreen_makeFlightFavorite_verifyFavoritesRoutesAreDisplayed() {
        composeTestRule
            .onNodeWithText(context.getString(R.string.enter_airport))
            .performTextInput(airportFCO.iAtaCode)
        composeTestRule.onNodeWithTag("${airportFCO.iAtaCode}-${airportFCO.name}").performClick()
        composeTestRule
            .onNodeWithTag("favorite-${airportFCO.iAtaCode}-${airportMUC.iAtaCode}")
            .performClick()
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.back_button)
        ).performClick()
        composeTestRule
            .onNodeWithText(context.getString(R.string.enter_airport))
            .performTextClearance()
        composeTestRule
            .onNodeWithText(context.getString(R.string.favorite_routes))
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("${R.string.depart}-${airportFCO.iAtaCode}-${airportFCO.name}")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("${R.string.arrive}-${airportMUC.iAtaCode}-${airportMUC.name}")
            .assertIsDisplayed()
    }

}