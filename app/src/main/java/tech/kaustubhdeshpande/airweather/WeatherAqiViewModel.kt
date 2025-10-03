package tech.kaustubhdeshpande.airweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val weather: WeatherEntity? = null,
    val aqi: AqiEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherAqiViewModel(
    private val repo: WeatherAqiRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun fetch() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val w = repo.refreshWeather()
                val a = repo.refreshAqi()
                _ui.value = UiState(weather = w, aqi = a, isLoading = false)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun saveCurrentData() {
        val weather = _ui.value.weather
        val aqi = _ui.value.aqi
        viewModelScope.launch {
            weather?.let { repo.saveWeather(it) }
            aqi?.let { repo.saveAqi(it) }
        }
    }

    suspend fun getAllSavedWeather(): List<WeatherEntity> = repo.getAllWeather()
    suspend fun getAllSavedAqi(): List<AqiEntity> = repo.getAllAqi()
}