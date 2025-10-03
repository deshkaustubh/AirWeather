package tech.kaustubhdeshpande.airweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

        val factory = WeatherAqiViewModelFactory(repo)
        val vm = ViewModelProvider(this, factory)[WeatherAqiViewModel::class.java]

        setContent {
            AirWeatherTheme {
                HomeScreen(viewModel = vm)
            }
        }
    }
}