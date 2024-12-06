package com.example.weatherappassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappassignment.data.repository.MainRepository
import com.example.weatherappassignment.domain.model.MainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _weatherData = MutableLiveData<List<MainModel>>()
    val weatherData: LiveData<List<MainModel>> get() = _weatherData

    private val _currentWeather = MutableLiveData<MainModel?>()
    val currentWeather: LiveData<MainModel?> get() = _currentWeather

    init {
        viewModelScope.launch {
            getWeatherForCities()
        }
    }

    fun getWeatherForCities(){
        viewModelScope.launch {
            try {
                val weatherList = repository.getWeather()
                _weatherData.postValue(weatherList)
            } catch (_: Exception) {
            }
        }
    }

    fun getWeatherByCoordinates() {
        viewModelScope.launch {
            try {
                val currentWeatherResponse = repository.getWeatherByCoordinates()
                _currentWeather.postValue(currentWeatherResponse)
                Log.d("aman",currentWeatherResponse!!.cityName)
            } catch (_: Exception) { }
        }
    }
}