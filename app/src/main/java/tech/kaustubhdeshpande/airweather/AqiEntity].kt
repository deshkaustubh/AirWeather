package tech.kaustubhdeshpande.airweather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aqi")
data class AqiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val aqi: Int,                 // US AQI (0..500)
    val category: String,         // Good, Moderate, etc.
    val mainPollutant: String,
    val lastUpdated: Long
)