package tech.kaustubhdeshpande.airweather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val lastUpdated: Long
)