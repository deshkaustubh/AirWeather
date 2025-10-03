package tech.kaustubhdeshpande.airweather

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WeatherEntity::class, AqiEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AirWeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun aqiDao(): AqiDao

    companion object {
        @Volatile
        private var INSTANCE: AirWeatherDatabase? = null

        fun getInstance(context: Context): AirWeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AirWeatherDatabase::class.java,
                    "airweather_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}