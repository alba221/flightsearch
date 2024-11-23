package com.learningcompose.flightsearch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.learningcompose.flightsearch.data.Airport

/** Autocomplete list based on user text Search input */
@Composable
fun FlightSearchAutocomplete(
    airportSearchList: List<Airport>,
    setPage: () -> Unit,
    setSelectedAirport: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    AutocompleteItems(
        airportList = airportSearchList,
        setPage = setPage,
        setSelectedAirport = setSelectedAirport,
        modifier = modifier
    )
}

@Composable
fun AutocompleteItems(
    airportList: List<Airport>,
    setPage: () -> Unit,
    setSelectedAirport: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val inPreviewMode = LocalInspectionMode.current
    val visibleState = remember {
        MutableTransitionState(inPreviewMode).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInHorizontally(
            animationSpec = spring(
                stiffness = StiffnessLow,
                dampingRatio = DampingRatioLowBouncy
            )
        )
    ) {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .imePadding()
        ) {
            itemsIndexed(airportList) { index, airport ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                setPage()
                                setSelectedAirport(airport)
                                keyboardController?.hide()
                            }
                        )
                ) {
                    Text(
                        text = airport.iAtaCode,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = airport.name,
                        Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
