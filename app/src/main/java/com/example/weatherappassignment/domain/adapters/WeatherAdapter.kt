package com.example.weatherappassignment.domain.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappassignment.R
import com.example.weatherappassignment.domain.model.MainModel

class WeatherAdapter(private var weatherList: List<MainModel>): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val description: TextView = itemView.findViewById(R.id.tv_weather_description)
        val temprature: TextView = itemView.findViewById(R.id.tv_temperature)
        val humidity: TextView = itemView.findViewById(R.id.tv_humidity)
        val windSpeed: TextView = itemView.findViewById(R.id.tv_wind_speed)
        val cityName: TextView = itemView.findViewById(R.id.tv_city_name)
        val postTime:TextView = itemView.findViewById(R.id.tv_fetched_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weathercard, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.description.text = "Description: ${weather.description}"
        holder.temprature.text = "Temperature: ${weather.temprature}°C"
        holder.humidity.text = "Humidity: ${weather.humidity}%"
        holder.windSpeed.text = "Wind Speed: ${weather.windSpeed} m/s"
        holder.cityName.text = "Location: ${weather.cityName}"
        holder.postTime.text = "Posted: ${weather.timePosted}"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newWeather: List<MainModel>){
        weatherList = newWeather
        notifyDataSetChanged()
    }
}

