package com.example.weatherappassignment.di

import android.content.Context
import androidx.room.Room
import com.example.weatherappassignment.data.dataStore.DataStoreManager
import com.example.weatherappassignment.data.local.WeatherDao
import com.example.weatherappassignment.data.local.WeatherDatabase
import com.example.weatherappassignment.data.remote.WeatherApi
import com.example.weatherappassignment.data.repository.MainRepository
import com.example.weatherappassignment.utils.Contants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule{

    @Singleton
    @Provides
    fun provideApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideDatabase( @ApplicationContext context: Context):WeatherDatabase{
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "user_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideMyDao(db: WeatherDatabase): WeatherDao {
        return db.weatherDao()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun providesMyRepository(
        @ApplicationContext context: Context,
        api: WeatherApi,
        dao: WeatherDao,
        dataStoreManager: DataStoreManager
    ): MainRepository {
        return MainRepository(
            context = context,
            weatherDao = dao,
            weatherApi = api,
            dataStoreManager = dataStoreManager
        )

    }

    @Provides
    @Singleton
    fun provideDataStorePreference(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)

    }
}