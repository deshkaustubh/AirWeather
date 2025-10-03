package tech.kaustubhdeshpande.airweather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherAqiRepository(
    private val weatherDao: WeatherDao,
    private val aqiDao: AqiDao,
    private val weatherApi: OpenMeteoWeatherApi,
    private val airApi: OpenMeteoAirQualityApi
) {
    private val lat = 19.1864
    private val lon = 72.8656
    private val fmt = DateTimeFormatter.ISO_DATE_TIME

    // Storage helpers
    suspend fun getCachedWeather(): WeatherEntity? = withContext(Dispatchers.IO) { weatherDao.getWeatherSync() }
    suspend fun getCachedAqi(): AqiEntity? = withContext(Dispatchers.IO) { aqiDao.getAqiSync() }
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        weatherDao.clear()
        aqiDao.clear()
    }

    suspend fun refreshWeather(): WeatherEntity? = withContext(Dispatchers.IO) {
        try {
            val r = weatherApi.getForecast(
                latitude = lat,
                longitude = lon,
                currentWeather = true,
                hourly = "relativehumidity_2m",
                timezone = "auto",
                pastDays = 1,
                forecastDays = 1
            )

            val temp = r.current_weather?.temperature
            val code = r.current_weather?.weathercode
            val hTimes = r.hourly?.time ?: emptyList()
            val humid = r.hourly?.relativehumidity_2m
            val idx = closestHourIndex(hTimes)
            val humidity = if (idx != null && humid != null && idx in humid.indices) humid[idx] else humid?.lastOrNull()

            if (temp != null && code != null && humidity != null) {
                val entity = WeatherEntity(
                    temperature = (temp * 10.0).roundToInt() / 10.0,
                    condition = weatherCodeToString(code),
                    humidity = humidity,
                    lastUpdated = System.currentTimeMillis()
                )
                weatherDao.insert(entity)
                entity
            } else {
                weatherDao.getWeatherSync()
            }
        } catch (e: Exception) {
            weatherDao.getWeatherSync()
        }
    }

    suspend fun refreshAqi(): AqiEntity? = withContext(Dispatchers.IO) {
        try {
            val r = airApi.getAirQuality(
                latitude = lat,
                longitude = lon,
                hourly = "pm2_5,pm10,ozone,nitrogen_dioxide,sulphur_dioxide,carbon_monoxide,us_aqi",
                timezone = "auto",
                pastDays = 1,
                forecastDays = 1
            )

            val h = r.hourly ?: return@withContext aqiDao.getAqiSync()
            val idx = closestHourIndex(h.time ?: emptyList())

            val usAqiAtIdx = idx?.let { i -> h.us_aqi?.getOrNull(i)?.roundToInt() }
            val pm25AtIdx = idx?.let { i -> h.pm2_5?.getOrNull(i) }
            val usAqi = usAqiAtIdx ?: pm25AtIdx?.let { computeUsAqiFromPm25(it) } ?: h.us_aqi?.lastOrNull()?.roundToInt()
                ?: h.pm2_5?.lastOrNull()?.let { computeUsAqiFromPm25(it) }

            if (usAqi == null) {
                return@withContext aqiDao.getAqiSync()
            }

            val dom = run {
                val pairs = listOfNotNull(
                    "PM2.5" to (pm25AtIdx ?: h.pm2_5?.lastOrNull()),
                    "PM10" to (idx?.let { i -> h.pm10?.getOrNull(i) } ?: h.pm10?.lastOrNull()),
                    "O3" to (idx?.let { i -> h.ozone?.getOrNull(i) } ?: h.ozone?.lastOrNull()),
                    "NO2" to (idx?.let { i -> h.nitrogen_dioxide?.getOrNull(i) } ?: h.nitrogen_dioxide?.lastOrNull()),
                    "SO2" to (idx?.let { i -> h.sulphur_dioxide?.getOrNull(i) } ?: h.sulphur_dioxide?.lastOrNull()),
                    "CO" to (idx?.let { i -> h.carbon_monoxide?.getOrNull(i) } ?: h.carbon_monoxide?.lastOrNull()),
                )
                pairs.filter { it.second != null }.maxByOrNull { it.second!! }?.first ?: "PM2.5"
            }

            val entity = AqiEntity(
                aqi = usAqi.coerceIn(0, 500),
                category = aqiCategory(usAqi),
                mainPollutant = dom,
                lastUpdated = System.currentTimeMillis()
            )
            aqiDao.insert(entity)
            entity
        } catch (_: Exception) {
            aqiDao.getAqiSync()
        }
    }

    suspend fun saveWeather(weather: WeatherEntity) = withContext(Dispatchers.IO) { weatherDao.insert(weather) }
    suspend fun saveAqi(aqi: AqiEntity) = withContext(Dispatchers.IO) { aqiDao.insert(aqi) }
    suspend fun getAllWeather(): List<WeatherEntity> = withContext(Dispatchers.IO) { weatherDao.getAll() }
    suspend fun getAllAqi(): List<AqiEntity> = withContext(Dispatchers.IO) { aqiDao.getAll() }

    private fun closestHourIndex(times: List<String>): Int? {
        if (times.isEmpty()) return null
        val now = LocalDateTime.now()
        var bestIdx = 0
        var bestDiff = Long.MAX_VALUE
        for (i in times.indices) {
            val t = runCatching { LocalDateTime.parse(times[i], fmt) }.getOrNull() ?: continue
            val diff = abs(java.time.Duration.between(t, now).toMillis())
            if (diff < bestDiff) {
                bestDiff = diff
                bestIdx = i
            }
        }
        return bestIdx
    }

    private fun computeUsAqiFromPm25(c: Double): Int {
        data class Bp(val clow: Double, val chigh: Double, val ilow: Int, val ihigh: Int)
        val bps = listOf(
            Bp(0.0, 12.0, 0, 50),
            Bp(12.1, 35.4, 51, 100),
            Bp(35.5, 55.4, 101, 150),
            Bp(55.5, 150.4, 151, 200),
            Bp(150.5, 250.4, 201, 300),
            Bp(250.5, 500.4, 301, 500)
        )
        val bp = bps.firstOrNull { c in it.clow..it.chigh } ?: bps.last()
        val aqi = (bp.ihigh - bp.ilow) * (c - bp.clow) / (bp.chigh - bp.clow) + bp.ilow
        return aqi.roundToInt().coerceIn(0, 500)
    }

    private fun weatherCodeToString(code: Int): String = when (code) {
        0 -> "Clear"
        1, 2 -> "Partly Cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing Drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing Rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow Grains"
        80, 81, 82 -> "Rain Showers"
        85, 86 -> "Snow Showers"
        95 -> "Thunderstorm"
        96, 97 -> "Thunderstorm w/ Hail"
        else -> "Unknown"
    }

    private fun aqiCategory(usAqi: Int): String = when (usAqi) {
        in 0..50 -> "Good"
        in 51..100 -> "Moderate"
        in 101..150 -> "Unhealthy for Sensitive"
        in 151..200 -> "Unhealthy"
        in 201..300 -> "Very Unhealthy"
        else -> "Hazardous"
    }
}