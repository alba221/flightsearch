package com.learningcompose.flightsearch.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.learningcompose.flightsearch.R
import com.learningcompose.flightsearch.data.Airport

object FlightResultsDestination : NavigationDestination {
    override val route = "flight_search_results"
    const val iAtaCode = "iata_code"
    val routeWithArgs = "$route/{$iAtaCode}"
}

/** Search results from Room DB */
@Composable
fun FlightSearchResults(
    flights: List<FlightDetail>,
    selectedAirport: Airport,
    onClickFavorite: (FlightDetail) -> Unit,
    onBackHandler: () -> Unit,
    modifier: Modifier = Modifier
) {

    BackHandler {
        onBackHandler()
    }

    FlightSearchScreen(
        selectedAirportIAtaCode = selectedAirport.iAtaCode,
        flights = flights,
        onClickFavorite = onClickFavorite,
        modifier = modifier
    )
}

@Composable
fun FlightSearchScreen(
    selectedAirportIAtaCode: String,
    flights: List<FlightDetail>,
    onClickFavorite: (FlightDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.flights_from, selectedAirportIAtaCode),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(flights) { index, flight ->
                FlightCard(
                    flight = flight,
                    onClickFavorite = onClickFavorite,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun FlightCard(
    flight: FlightDetail,
    onClickFavorite: (FlightDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                FlightDetailRow(
                    flightType = R.string.depart,
                    flight.airportDepart
                )
                FlightDetailRow(
                    R.string.arrive,
                    flight.airportArrive
                )
            }
            IconButton(
                onClick = {
                    onClickFavorite(flight)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.favorite),
                    tint = if (flight.isFavorite) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun FlightDetailRow(
    @StringRes flightType: Int,
    airport: Airport,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(flightType).uppercase(),
            style = MaterialTheme.typography.bodyMedium
        )
        Row {
            Text(
                text = airport.iAtaCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = airport.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

    }
}

