package tech.kaustubhdeshpande.airweather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE id = 0 LIMIT 1")
    fun getWeatherSync(): WeatherEntity?

    @Insert
    suspend fun insert(weather: WeatherEntity)

    @Query("DELETE FROM weather")
    suspend fun clear()

    @Query("SELECT * FROM weather")
    fun getAll(): List<WeatherEntity>
}