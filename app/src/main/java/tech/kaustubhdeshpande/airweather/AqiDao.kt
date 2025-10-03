package tech.kaustubhdeshpande.airweather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AqiDao {
    @Query("SELECT * FROM aqi WHERE id = 0 LIMIT 1")
    fun getAqiSync(): AqiEntity?

    @Insert
    suspend fun insert(aqi: AqiEntity)

    @Query("DELETE FROM aqi")
    suspend fun clear()

    @Query("SELECT * FROM aqi")
    fun getAll(): List<AqiEntity>
}