package com.example.weatherappassignment

import android.annotation.SuppressLint
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappassignment.databinding.ActivityMainBinding
import com.example.weatherappassignment.domain.adapters.WeatherAdapter
import com.example.weatherappassignment.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            checkLocationServicesAndFetchWeather()
            observeViewModel()
        } else {
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                startActivity(this)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestLocationPermission.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        weatherAdapter = WeatherAdapter(emptyList())
        recyclerView.adapter = weatherAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.currentWeather.observe(this) { response ->
            response?.let {
                binding.weatherCard.tvWeatherDescription.text = it.description
                binding.weatherCard.tvTemperature.text = "${it.temprature}°C"
                binding.weatherCard.tvHumidity.text = "Humidity: ${it.humidity}%"
                binding.weatherCard.tvWindSpeed.text = "Wind Speed: ${it.windSpeed} m/s"
                binding.weatherCard.tvCityName.text = "Location: ${it.cityName}"
                binding.weatherCard.tvFetchedTime.text = "Posted: ${it.timePosted}"
            }
        }

        viewModel.weatherData.observe(this) { weatherList ->
            weatherAdapter.updateData(weatherList)
        }
    }

    private fun checkLocationServicesAndFetchWeather() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isGpsEnabled || isNetworkEnabled) {
            lifecycleScope.launch {
                viewModel.getWeatherByCoordinates()
                Log.d("aman", "inside checkLocationServicesAndFetchWeather")
                observeViewModel()
            }
        } else {

            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocationServicesAndFetchWeather()
    }
}
