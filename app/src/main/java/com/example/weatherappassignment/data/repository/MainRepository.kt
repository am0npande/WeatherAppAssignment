package com.example.weatherappassignment.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.weatherappassignment.data.dataStore.DataStoreManager
import com.example.weatherappassignment.data.local.WeatherDao
import com.example.weatherappassignment.data.remote.WeatherApi
import com.example.weatherappassignment.domain.model.MainModel
import com.example.weatherappassignment.utils.Contants.API_KEY
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class MainRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val weatherDao: WeatherDao,
    private val weatherApi: WeatherApi,
    private val dataStoreManager: DataStoreManager
) {

    val listOfCities = listOf("New York", "Singapore", "Mumbai", "Delhi", "Sydney", "Melbourne")

    suspend fun getWeather(): List<MainModel> {


        if (!isInternetAvailable(context)) {
            return getAllWeather()
        }

        listOfCities.forEach {
            val response = weatherApi.getWeather(it, API_KEY)
            val convertResponse = MainModel(
                cityName = response.name,
                description = response.weather[0].description,
                temprature = response.main.temp.toString(),
                humidity = response.main.humidity.toString(),
                windSpeed = response.wind.speed.toString(),
                timePosted = getTime().toString()
            )
            insertWeather(convertResponse)
        }
        return getAllWeather()
    }

    private fun getTime(): String? {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
        return sdf.format(currentTime)
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getWeatherByCoordinates(): MainModel? {
        try {
            if (!isInternetAvailable(context)) {
                Log.d("aman", "Internet not available")
                return dataStoreManager.getCurrentWeather()
            }

            if (!isLocationEnabled(context)) {
                Log.d("aman", "Location not enabled")
                return dataStoreManager.getCurrentWeather()
            }

            // Use FusedLocationProviderClient for better location accuracy and fallback
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val location: Location? = try {
                fusedLocationProviderClient.lastLocation.await()
            } catch (e: Exception) {
                Log.e("aman", "Error fetching location: ${e.message}", e)
                null
            }

            if (location != null) {
                Log.d("aman", "Fetched location: Lat=${location.latitude}, Lon=${location.longitude}")
                val response = weatherApi.getWeatherByCordinates(
                    location.latitude.toString(),
                    location.longitude.toString(),
                    API_KEY
                )
                val currentWeather = MainModel(
                    cityName = response.name,
                    description = response.weather[0].description,
                    temprature = response.main.temp.toString(),
                    humidity = response.main.humidity.toString(),
                    windSpeed = response.wind.speed.toString(),
                    timePosted = getTime().toString()
                )
                dataStoreManager.saveCurrentWeather(currentWeather)
                return currentWeather
            } else {
                Log.d("aman", "Location is null. Fetching saved data.")
                return dataStoreManager.getCurrentWeather()
            }
        } catch (e: Exception) {
            Log.e("aman", "Error during weather API call: ${e.message}", e)
            return dataStoreManager.getCurrentWeather()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

        suspend fun insertWeather(weather: MainModel) {
        weatherDao.insertWeather(weather)
    }

    suspend fun getAllWeather(): List<MainModel> {
        return weatherDao.getAllWeather()
    }
}
