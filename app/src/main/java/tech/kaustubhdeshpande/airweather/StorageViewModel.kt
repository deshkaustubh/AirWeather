package tech.kaustubhdeshpande.airweather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StorageUiState(
    val dbPath: String = "",
    val weather: WeatherEntity? = null,
    val aqi: AqiEntity? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)

class StorageViewModel(
    private val repo: WeatherAqiRepository,
    appContext: Context
) : ViewModel() {

    private val _ui = MutableStateFlow(
        StorageUiState(
            dbPath = appContext.getDatabasePath("airweather_db").absolutePath
        )
    )
    val ui: StateFlow<StorageUiState> = _ui

    init {
        refresh()
    }

    fun refresh() {
        _ui.value = _ui.value.copy(isLoading = true, message = null)
        viewModelScope.launch {
            val w = repo.getCachedWeather()
            val a = repo.getCachedAqi()
            _ui.value = _ui.value.copy(
                weather = w,
                aqi = a,
                isLoading = false
            )
        }
    }

    fun clear() {
        _ui.value = _ui.value.copy(isLoading = true, message = null)
        viewModelScope.launch {
            repo.clearCache()
            val w = repo.getCachedWeather()
            val a = repo.getCachedAqi()
            _ui.value = _ui.value.copy(
                weather = w,
                aqi = a,
                isLoading = false,
                message = "Cache cleared"
            )
        }
    }
}