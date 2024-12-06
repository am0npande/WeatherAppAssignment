package com.example.weatherappassignment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherappassignment.domain.model.MainModel

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: MainModel)

    @Query("SELECT * FROM weather_table")
    suspend fun getAllWeather(): List<MainModel>
}