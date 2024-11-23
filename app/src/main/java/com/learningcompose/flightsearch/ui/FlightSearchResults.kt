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
import com.learningcompose.flightsearch.data.Favorite

/** Search results from Room DB */
@Composable
fun FlightSearchResults(
    airportDepart: Airport?,
    airports: List<Airport>,
    favoriteRoutes: List<Favorite>,
    selectedAirportIAtaCode: String,
    insertFavorite: (Favorite) -> Unit,
    deleteFavorite: (Favorite) -> Unit,
    onBackHandler: () -> Unit,
    modifier: Modifier = Modifier
) {

    BackHandler {
        onBackHandler()
    }

    FlightSearchScreen(
        selectedAirportIAtaCode = selectedAirportIAtaCode,
        airportDepart = airportDepart,
        airports = airports,
        favoriteRoutes = favoriteRoutes,
        onClickFavorite = { favorite, airportDepart, airportArrive ->
            if (isFavoriteRoute(airportDepart, airportArrive, favoriteRoutes)) {
                deleteFavorite(favorite)
            } else {
                insertFavorite(favorite)
            }

        },
        modifier = modifier
    )
}

@Composable
fun FlightSearchScreen(
    selectedAirportIAtaCode: String,
    airportDepart: Airport?,
    airports: List<Airport>,
    favoriteRoutes: List<Favorite>,
    onClickFavorite: (Favorite, Airport, Airport) -> Unit,
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
            itemsIndexed(airports) { index, airport ->
                if (airport.iAtaCode != selectedAirportIAtaCode) {
                    FlightCard(
                        airportDepart = airportDepart,
                        airportArrive = airport,
                        isFavorite = isFavoriteRoute(airportDepart, airport, favoriteRoutes),
                        onClickFavorite = onClickFavorite,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FlightCard(
    airportDepart: Airport?,
    airportArrive: Airport,
    isFavorite: Boolean = false,
    onClickFavorite: (Favorite, Airport, Airport) -> Unit,
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
                    airportDepart ?: defaultAirport
                )
                FlightDetailRow(
                    R.string.arrive,
                    airportArrive
                )
            }
            IconButton(
                onClick = {
                    onClickFavorite(
                        Favorite(0, airportDepart?.iAtaCode ?: "", airportArrive.iAtaCode),
                        airportDepart ?: defaultAirport,
                        airportArrive
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.favorite),
                    tint = if (isFavorite) {
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

fun isFavoriteRoute(
    airportDepart: Airport?,
    airportArrive: Airport,
    favoriteRoutes: List<Favorite>
): Boolean {
    for (route in favoriteRoutes) {
        if (airportDepart?.iAtaCode == route.departureCode
            && airportArrive.iAtaCode == route.destinationCode) {
            return true
        }
    }
    return false
}
