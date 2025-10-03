package tech.kaustubhdeshpande.airweather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    viewModel: StorageViewModel,
    onBack: () -> Unit
) {
    val ui by viewModel.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Database", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(text = "Path: ${ui.dbPath}", style = MaterialTheme.typography.bodySmall)

            Divider()

            Text("Weather (cached)", style = MaterialTheme.typography.titleMedium)
            ui.weather?.let { w ->
                Text("Temp: ${w.temperature} °C")
                Text("Cond: ${w.condition}")
                Text("Humidity: ${w.humidity}%")
                Text("Updated: ${w.lastUpdated}")
            } ?: Text("No weather row in DB")

            Text("Air Quality (cached)", style = MaterialTheme.typography.titleMedium)
            ui.aqi?.let { a ->
                Text("US AQI: ${a.aqi} (${a.category})")
                Text("Main pollutant: ${a.mainPollutant}")
                Text("Updated: ${a.lastUpdated}")
            } ?: Text("No AQI row in DB")

            RowWithActions(
                isLoading = ui.isLoading,
                onRefresh = { viewModel.refresh() },
                onClear = { viewModel.clear() }
            )

            ui.message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
private fun RowWithActions(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onClear: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = onRefresh) { Text("Refresh") }
        OutlinedButton(onClick = onClear) { Text("Clear cache") }
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(start = 8.dp))
        }
    }
}