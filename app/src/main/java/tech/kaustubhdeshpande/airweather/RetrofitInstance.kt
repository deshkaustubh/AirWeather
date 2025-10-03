package tech.kaustubhdeshpande.airweather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val weatherRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val airQualityRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApi: OpenMeteoWeatherApi by lazy {
        weatherRetrofit.create(OpenMeteoWeatherApi::class.java)
    }

    val airQualityApi: OpenMeteoAirQualityApi by lazy {
        airQualityRetrofit.create(OpenMeteoAirQualityApi::class.java)
    }
}