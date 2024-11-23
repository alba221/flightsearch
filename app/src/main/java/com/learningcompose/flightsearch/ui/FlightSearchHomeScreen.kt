package com.learningcompose.flightsearch.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learningcompose.flightsearch.R
import com.learningcompose.flightsearch.data.Airport
import com.learningcompose.flightsearch.data.Favorite
import com.learningcompose.flightsearch.ui.theme.FlightSearchTheme

enum class PageType {
    AutocompleteList, SearchResults
}

val defaultAirport = Airport(-1, "", "", 0)

/**
 * Home screen displaying Search bar and autocomplete list of airports.
 * If Search bar text is empty and favorites exists, then the list of
 * favorite flights are displayed
 * */
@Composable
fun FlightSearchHomeScreen(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    /** Search text stored in Preferences DataStore */
    val searchTextFromPrefs by viewModel.getSearchText().collectAsState("")

    var searchText by remember { mutableStateOf("") }

    /** Helper variable for loading Search text from Preferences DataStore */
    var searchTextLoaded by remember { mutableStateOf(false) }

    /** Selected IATA code from autocomplete list */
    var selectedAirportIAtaCode by rememberSaveable { mutableStateOf("") }

    /** All airports from Room DB */
    val allAirport by viewModel.getAllAirports().collectAsState(emptyList())

    /** All favorite routes (flights) from Room DB */
    val favoriteRoutes by viewModel.getAllFavorites().collectAsState(emptyList())
    val onBackHandler = {
        navController.popBackStack()
        selectedAirportIAtaCode = ""
    }

    Scaffold(
        topBar= {
            FlightSearchTopAppBar(
                canNavigateBack = selectedAirportIAtaCode.isNotEmpty(),
                navigateUp = onBackHandler
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.saveSearchPrefsText(it)
                },
                shape = RoundedCornerShape(size = 50.dp),
                label = {
                    Text(
                        text = stringResource(R.string.enter_airport)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = stringResource(R.string.voice),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                },
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )

            if (searchTextFromPrefs.isNotEmpty()) {
                if (!searchTextLoaded) {
                    searchText = searchTextFromPrefs
                    searchTextLoaded = true
                }

                /** Depart airport based on selected value from autocomplete list */
                val airportDepart: Airport? by viewModel.getAirportByIAtaCode(selectedAirportIAtaCode)
                    .collectAsState(defaultAirport)

                /** Autocomplete list based on user text input in Search bar */
                val airportAutoCompleteSearchList by viewModel.searchAirports(
                    airportName = "%$searchTextFromPrefs%",
                    iAtaCode = searchTextFromPrefs.uppercase()
                ).collectAsState(emptyList())

                /**
                 * FlightSearch Navigation. When one option is selected from autocomplete list
                 * you can navigate back to the list.
                 * */
                NavHost(
                    navController = navController,
                    startDestination = PageType.AutocompleteList.name
                ) {
                    composable(PageType.AutocompleteList.name) {
                        FlightSearchAutocomplete(
                            airportSearchList = airportAutoCompleteSearchList,
                            setPage = {
                                navController.navigate(PageType.SearchResults.name)
                            },
                            setSelectedAirport = {
                                selectedAirportIAtaCode = it.iAtaCode
                            }
                        )

                    }
                    composable(PageType.SearchResults.name) {
                        FlightSearchResults(
                            airportDepart = airportDepart,
                            airports = allAirport,
                            favoriteRoutes = favoriteRoutes,
                            selectedAirportIAtaCode = selectedAirportIAtaCode,
                            insertFavorite = viewModel::insertFavorite,
                            deleteFavorite = viewModel::deleteFavorite,
                            onBackHandler = onBackHandler
                        )
                    }
                }
            } else {
                FlightSearchFavoriteRoutes(
                    airports = allAirport,
                    favoriteRoutes = favoriteRoutes,
                    onClickFavorite = { favorite, airportDepart, airportArrive ->
                        if (isFavoriteRoute(airportDepart, airportArrive, favoriteRoutes)) {
                            viewModel.deleteFavorite(favorite)
                        } else {
                            viewModel.insertFavorite(favorite)
                        }
                    },
                    searchText = searchText,
                    navController = navController,
                    onBackHandler = onBackHandler
                )
            }
        }
    }


}

fun NavController.isOnBackStack(route: String): Boolean =
    try { getBackStackEntry(route); true } catch(_: Throwable) { false }

val airportList = listOf(
    Airport(1, "Vienna International Airport", "VIE", 7812938),
    Airport(2, "Sheremetyevo - A.S. Pushkin international airport", "SVO", 49933000),
    Airport(3, "Leonardo da Vinci International Airport", "FCO", 11662842)
)

@Composable
@Preview(showBackground = true)
fun FlightSearchAutocompletePreview() {
    FlightSearchTheme {
        FlightSearchAutocomplete(
            airportList,
            {},
            {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun FlightSearchResultsPreview() {
    FlightSearchTheme {
        val selectedAirportIAtaCode = "ATH"
        val airportDepart = Airport(4, "Athens International Airport", "ATH", 12345786)
        val favoriteRoutes = listOf(
            Favorite(1, "ATH", "FCO")
        )
        FlightSearchResults(
            airportDepart,
            airportList,
            favoriteRoutes,
            selectedAirportIAtaCode,
            onBackHandler = {},
            insertFavorite = {},
            deleteFavorite = {}
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview(showBackground = true)
fun FlightSearchFavoriteRoutesPreview() {

    FlightSearchTheme {
        val favoriteRoutes = listOf(
            Favorite(1, "VIE", "FCO"),
            Favorite(2, "VIE", "SVO")
        )
        FlightSearchFavoriteRoutes(
            airportList,
            favoriteRoutes,
            onClickFavorite = {a,b,c ->},
            "",
            rememberNavController(),
            onBackHandler = {}
        )
    }

}