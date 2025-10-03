package tech.kaustubhdeshpande.airweather

import retrofit2.http.GET
import retrofit2.http.Query

// Open-Meteo Weather API DTOs
data class ForecastResponse(
    val current_weather: CurrentWeather?,
    val hourly: Hourly?
) {
    data class CurrentWeather(
        val temperature: Double,      // Celsius
        val weathercode: Int,         // WMO code
        val time: String              // ISO timestamp
    )
    data class Hourly(
        val time: List<String>,
        val relativehumidity_2m: List<Int>?
    )
}

interface OpenMeteoWeatherApi {
    // https://api.open-meteo.com/v1/forecast?latitude=..&longitude=..&current_weather=true&hourly=relativehumidity_2m&timezone=auto&past_days=1&forecast_days=1
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "relativehumidity_2m",
        @Query("timezone") timezone: String = "auto",
        @Query("past_days") pastDays: Int = 1,
        @Query("forecast_days") forecastDays: Int = 1
    ): ForecastResponse
}

// Open-Meteo Air Quality API DTOs
data class AirQualityHourly(
    val time: List<String>? = emptyList(),
    val pm2_5: List<Double>? = emptyList(),
    val pm10: List<Double>? = emptyList(),
    val ozone: List<Double>? = emptyList(),                // O3
    val nitrogen_dioxide: List<Double>? = emptyList(),     // NO2
    val sulphur_dioxide: List<Double>? = emptyList(),      // SO2
    val carbon_monoxide: List<Double>? = emptyList(),      // CO
    // Use Double to be tolerant; we convert to Int later.
    val us_aqi: List<Double>? = emptyList()
)

data class AirQualityResponseOM(
    val hourly: AirQualityHourly?
)

interface OpenMeteoAirQualityApi {
    // https://air-quality-api.open-meteo.com/v1/air-quality?latitude=..&longitude=..&hourly=...&timezone=auto&past_days=1&forecast_days=1
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "pm2_5,pm10,ozone,nitrogen_dioxide,sulphur_dioxide,carbon_monoxide,us_aqi",
        @Query("timezone") timezone: String = "auto",
        @Query("past_days") pastDays: Int = 1,
        @Query("forecast_days") forecastDays: Int = 1
    ): AirQualityResponseOM
}