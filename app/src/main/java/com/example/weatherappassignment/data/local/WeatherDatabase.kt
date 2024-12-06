package com.example.weatherappassignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherappassignment.domain.model.MainModel


@Database(entities = [MainModel::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}