package com.example.weatherappassignment.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class MainModel(
    val description: String,
    val temprature: String,
    val humidity: String,
    val windSpeed: String,
    @PrimaryKey
    val cityName: String,
    val timePosted:String
)