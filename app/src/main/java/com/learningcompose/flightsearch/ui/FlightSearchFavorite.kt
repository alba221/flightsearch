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

/** Favorite routes (flights) */
@Composable
fun FlightSearchFavoriteRoutes(
    flights: List<FlightDetail>,
    onClickFavorite: (FlightDetail) -> Unit,
    searchText: String,
    navController: NavHostController,
    onBackHandler: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (searchText.isEmpty() && navController.isOnBackStack(FlightAutocompleteDestination.route)) {
        onBackHandler()
        navController.navigate(FlightAutocompleteDestination.route) {
            launchSingleTop = true
        }
    }

    /** If in Preview mode don't show animation. */
    val inPreviewMode = LocalInspectionMode.current
    val visibleState = remember {
        MutableTransitionState(inPreviewMode).apply {
            targetState = true
        }
    }

    if (!flights.isEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.favorite_routes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(flights) { index, flight ->
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
                            flight = flight,
                            onClickFavorite = onClickFavorite,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}