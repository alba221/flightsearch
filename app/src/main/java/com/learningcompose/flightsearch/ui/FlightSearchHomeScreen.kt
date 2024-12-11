package com.learningcompose.flightsearch.ui

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.learningcompose.flightsearch.R
import com.learningcompose.flightsearch.data.Airport
import com.learningcompose.flightsearch.ui.theme.FlightSearchTheme

interface NavigationDestination {
    val route: String
}

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
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    var searchText = viewModel.searchText
    val navController = rememberNavController()

    val onBackHandler = {
        navController.popBackStack()
        viewModel.removeSelectedAirport()
    }

    homeUiState.errorMessage?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_LONG).show()
    }

    Scaffold(
        topBar= {
            FlightSearchTopAppBar(
                canNavigateBack = homeUiState.selectedDepartAirport != null,
                navigateUp = onBackHandler
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {

            TopSearchFlightBar(
                searchText = searchText,
                onValueChange = { viewModel.updateSearchText(it) }
            )

            if (searchText.isNotEmpty()) {
                /**
                 * FlightSearch Navigation. When one option is selected from autocomplete list
                 * you can navigate back to the list.
                 * */
                NavHost(
                    navController = navController,
                    startDestination = FlightAutocompleteDestination.route
                ) {
                    composable(route = FlightAutocompleteDestination.route) {
                        FlightSearchAutocompleteItems(
                            airportSearchList = homeUiState.autocompleteList,
                            navigateToFlights = {
                                navController.navigate("${FlightResultsDestination.route}/$it")
                            }
                        )

                    }
                    composable(
                        route = FlightResultsDestination.routeWithArgs,
                        arguments = listOf(navArgument(FlightResultsDestination.iAtaCode) {
                            type = NavType.StringType
                        })
                    ) { navBackStackEntry ->
                        val iAtaCode = navBackStackEntry.arguments?.getString(FlightResultsDestination.iAtaCode) ?: ""
                        LaunchedEffect(Unit) {
                            viewModel.setSelectedAirport(iAtaCode)
                        }
                        FlightSearchResults(
                            flights = homeUiState.flights,
                            selectedAirport = homeUiState.selectedDepartAirport ?: defaultAirport,
                            onClickFavorite = viewModel::onClickFavorite,
                            onBackHandler = onBackHandler
                        )
                    }
                }
            } else {
                FlightSearchFavoriteRoutes(
                    flights = homeUiState.favoriteRoutes,
                    onClickFavorite = viewModel::onClickFavoriteList,
                    searchText = searchText,
                    navController = navController,
                    onBackHandler = onBackHandler
                )
            }
        }
    }
}

@Composable
fun TopSearchFlightBar(
    searchText: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onValueChange,
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
}

fun NavController.isOnBackStack(route: String): Boolean =
    try { getBackStackEntry(route); true } catch(_: Throwable) { false }

val airportAutocompleteList = listOf(
    AirportAutocomplete("VIE", "Vienna International Airport"),
    AirportAutocomplete("SVO", "Sheremetyevo - A.S. Pushkin international airport"),
    AirportAutocomplete("FCO", "Leonardo da Vinci International Airport")
)
val flightsList = listOf(
    FlightDetail(
        Airport(1, "Vienna International Airport", "VIE", 7812938),
        Airport(2, "Sheremetyevo - A.S. Pushkin international airport", "SVO", 49933000),
        false
    ),
    FlightDetail(
        Airport(1, "Vienna International Airport", "VIE", 7812938),
        Airport(3, "Leonardo da Vinci International Airport", "FCO", 11662842),
        true
    )
)

@Composable
@Preview(showBackground = true)
fun FlightSearchAutocompletePreview() {
    FlightSearchTheme {
        FlightSearchAutocompleteItems(
            airportAutocompleteList,
            {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun FlightSearchResultsPreview() {
    FlightSearchTheme {
        FlightSearchResults(
            flightsList,
            defaultAirport,
            onBackHandler = {},
            onClickFavorite = {}
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview(showBackground = true)
fun FlightSearchFavoriteRoutesPreview() {
    FlightSearchTheme {
        FlightSearchFavoriteRoutes(
            flightsList,
            onClickFavorite = {},
            "",
            rememberNavController(),
            onBackHandler = {}
        )
    }
}