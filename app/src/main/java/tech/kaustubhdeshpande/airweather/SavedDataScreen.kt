package tech.kaustubhdeshpande.airweather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedDataScreen(
    viewModel: WeatherAqiViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val weatherList = remember { mutableStateOf<List<WeatherEntity>>(emptyList()) }
    val aqiList = remember { mutableStateOf<List<AqiEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            weatherList.value = viewModel.getAllSavedWeather().sortedByDescending { it.lastUpdated }
            aqiList.value = viewModel.getAllSavedAqi().sortedByDescending { it.lastUpdated }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Data") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier.padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Saved Weather Entries", style = MaterialTheme.typography.titleMedium)
            }
            if (weatherList.value.isEmpty()) {
                item {
                    Text("No saved weather data.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(weatherList.value) { w ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Temp: ${w.temperature}°C, Condition: ${w.condition}", style = MaterialTheme.typography.bodyMedium)
                            Text("Humidity: ${w.humidity}%", style = MaterialTheme.typography.bodySmall)
                            Text("Updated: ${formatTime(w.lastUpdated)}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                Text("Saved AQI Entries", style = MaterialTheme.typography.titleMedium)
            }
            if (aqiList.value.isEmpty()) {
                item {
                    Text("No saved AQI data.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(aqiList.value) { a ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("AQI: ${a.aqi}, Category: ${a.category}", style = MaterialTheme.typography.bodyMedium)
                            Text("Main Pollutant: ${a.mainPollutant}", style = MaterialTheme.typography.bodySmall)
                            Text("Updated: ${formatTime(a.lastUpdated)}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    return try {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        sdf.format(java.util.Date(ms))
    } catch (_: Exception) {
        "-"
    }
}
