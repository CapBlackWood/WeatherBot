package data.remote.repository

import api.ReverseGeocodingAPI
import api.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import data.remote.models.CurrentWeather
import data.remote.models.ReversedCountry

class WeatherRepository(
    private val weatherApi: WeatherApi,
    private val reverseGeocodingAPI: ReverseGeocodingAPI
) {

    suspend fun getCurrentWeather(apiKey: String, countryName: String, airQualityData: String): CurrentWeather {
        return withContext(Dispatchers.IO) {
            weatherApi.getCurrentWeather(apiKey, countryName, airQualityData)
        }.await()
    }

    suspend fun getReverseGeoCodingCountryName(latitude: String, longitude: String, format: String): ReversedCountry {
        return withContext(Dispatchers.IO) {
            reverseGeocodingAPI.getCountryNameByCoordinates(latitude, longitude, format)
        }.await()
    }
}