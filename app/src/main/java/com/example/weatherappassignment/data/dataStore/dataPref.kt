package com.example.weatherappassignment.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherappassignment.domain.model.MainModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCurrentWeather(weather: MainModel) {
        val json = gson.toJson(weather)
        sharedPreferences.edit().putString("current_weather", json).apply()
    }

    fun getCurrentWeather(): MainModel? {
        val json = sharedPreferences.getString("current_weather", null)
        return if (json != null) gson.fromJson(json, MainModel::class.java) else null
    }
}