package tech.kaustubhdeshpande.airweather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StorageViewModelFactory(
    private val repo: WeatherAqiRepository,
    private val appContext: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            return StorageViewModel(repo, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}