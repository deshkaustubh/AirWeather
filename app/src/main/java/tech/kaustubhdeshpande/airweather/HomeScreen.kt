package tech.kaustubhdeshpande.airweather

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: WeatherAqiViewModel,
    onOpenStorage: () -> Unit = {}
) {
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    Scaffold { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("AirWeather", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Kandivali East, Thakur Village", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(20.dp))

            // Weather card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Weather", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    ui.weather?.let { w ->
                        Text("${w.temperature}°C • ${w.condition}", style = MaterialTheme.typography.titleMedium)
                        Text("Humidity: ${w.humidity}%", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("Updated: ${formatTime(w.lastUpdated)}", style = MaterialTheme.typography.bodySmall)
                    } ?: Text("No weather data yet")
                }
            }

            Spacer(Modifier.height(12.dp))

            // AQI card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Air Quality", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    ui.aqi?.let { a ->
                        Text("US AQI: ${a.aqi} (${a.category})", style = MaterialTheme.typography.titleMedium)
                        Text("Main Pollutant: ${a.mainPollutant}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("Updated: ${formatTime(a.lastUpdated)}", style = MaterialTheme.typography.bodySmall)
                    } ?: Text("No AQI data yet")
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { viewModel.fetch() }) { Text("Refresh") }
                OutlinedButton(onClick = onOpenStorage) { Text("Storage") }
                if (ui.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }

            ui.error?.let {
                Spacer(Modifier.height(12.dp))
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(ms))
    } catch (_: Exception) {
        "-"
    }
}