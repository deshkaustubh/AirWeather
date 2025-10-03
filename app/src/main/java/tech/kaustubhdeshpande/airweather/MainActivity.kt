package tech.kaustubhdeshpande.airweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import tech.kaustubhdeshpande.airweather.ui.theme.AirWeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Build minimal dependencies (Open-Meteo + Room)
        val db = AirWeatherDatabase.getInstance(applicationContext)
        val weatherApi = RetrofitInstance.weatherApi
        val airApi = RetrofitInstance.airQualityApi
        val repo = WeatherAqiRepository(
            weatherDao = db.weatherDao(),
            aqiDao = db.aqiDao(),
            weatherApi = weatherApi,
            airApi = airApi
        )

        // ViewModels
        val weatherFactory = WeatherAqiViewModelFactory(repo)
        val weatherVm = ViewModelProvider(this, weatherFactory)[WeatherAqiViewModel::class.java]

        val storageFactory = StorageViewModelFactory(repo, applicationContext)
        val storageVm = ViewModelProvider(this, storageFactory)[StorageViewModel::class.java]

        setContent {
            AirWeatherTheme {
                var showStorage by rememberSaveable { mutableStateOf(false) }
                if (showStorage) {
                    SavedDataScreen(
                        viewModel = weatherVm,
                        onBack = { showStorage = false }
                    )
                } else {
                    HomeScreen(
                        viewModel = weatherVm,
                        onOpenStorage = { showStorage = true }
                    )
                }
            }
        }
    }
}