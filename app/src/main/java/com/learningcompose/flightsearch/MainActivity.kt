package com.learningcompose.flightsearch

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.learningcompose.flightsearch.ui.FlightSearchApp
import com.learningcompose.flightsearch.ui.theme.FlightSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** CurrentNightMode is used to set correct status bar style */
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        enableEdgeToEdge(
            statusBarStyle = if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                SystemBarStyle.dark(Color.Transparent.toArgb())
            } else {
                SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
            }
        )
        setContent {
            FlightSearchTheme {
                Surface {
                    FlightSearchApp()
                }
            }
        }
    }
}
