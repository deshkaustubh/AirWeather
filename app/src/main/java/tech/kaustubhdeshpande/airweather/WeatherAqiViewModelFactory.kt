package tech.kaustubhdeshpande.airweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherAqiViewModelFactory(
    private val repo: WeatherAqiRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAqiViewModel::class.java)) {
            return WeatherAqiViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}