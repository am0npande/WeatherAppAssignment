package com.example.weatherappassignment.data.remote

import com.example.weatherappassignment.data.model.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"

    ): Response

    @GET("weather")
    suspend fun getWeatherByCordinates(
        @Query("lat") latitude:String,
        @Query("lon") longitude:String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response

}