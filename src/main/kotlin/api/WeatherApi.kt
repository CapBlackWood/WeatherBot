package api

import kotlinx.coroutines.Deferred
import data.remote.models.CurrentWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") countryName: String,
        @Query("aqi") airQualityData: String,
    ): Deferred<CurrentWeather>


}