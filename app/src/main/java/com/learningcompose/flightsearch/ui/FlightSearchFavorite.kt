package com.learningcompose.flightsearch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.learningcompose.flightsearch.R
import com.learningcompose.flightsearch.data.Airport
import com.learningcompose.flightsearch.data.Favorite

/** Favorite routes (flights) */
@Composable
fun FlightSearchFavoriteRoutes(
    airports: List<Airport>,
    favoriteRoutes: List<Favorite>,
    onClickFavorite: (Favorite, Airport, Airport) -> Unit,
    searchText: String,
    navController: NavHostController,
    onBackHandler: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (searchText.isEmpty() && navController.isOnBackStack(PageType.AutocompleteList.name)) {
        onBackHandler()
        navController.navigate(PageType.AutocompleteList.name) {
            launchSingleTop = true
        }
    }

    val inPreviewMode = LocalInspectionMode.current
    val visibleState = remember {
        MutableTransitionState(inPreviewMode).apply {
            targetState = true
        }
    }

    if (!favoriteRoutes.isEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.favorite_routes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(favoriteRoutes) { index, route ->
                    val airportDepart = getAirportByIAtaCode(route.departureCode, airports)
                    val airportArrive = getAirportByIAtaCode(route.destinationCode, airports)
                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = slideInVertically(
                            animationSpec = spring(
                                stiffness = StiffnessLow,
                                dampingRatio = DampingRatioLowBouncy
                            ),
                            initialOffsetY = { it * 2 }
                        )
                    ) {
                        FlightCard(
                            airportDepart = airportDepart,
                            airportArrive = airportArrive,
                            isFavorite = isFavoriteRoute(airportDepart, airportArrive, favoriteRoutes),
                            onClickFavorite = onClickFavorite,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                }
            }
        }
    }
}

private fun getAirportByIAtaCode(iAtaCode: String, airports: List<Airport>): Airport {
    for (airport in airports) {
        if (airport.iAtaCode == iAtaCode) {
            return airport
        }
    }
    return defaultAirport
}