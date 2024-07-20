package data.remote

import api.ReverseGeocodingAPI
import api.WeatherApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val WEATHER_BASE_URL = "http://api.weatherapi.com/v1/"
private const val REVERSE_GEOCODER_BASE_URL = "https://nominatim.openstreetmap.org/"
val API_KEY = "2dbb071028094e9db1c165506241807"

enum class RetrofitType(val baseUrl: String) {
    WEATHER(WEATHER_BASE_URL),
    REVERSE_GEOCODER(REVERSE_GEOCODER_BASE_URL)
}

class RetrofitClient {

    fun getClient(): OkHttpClient{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
        return okHttpClient.build()
    }

    fun getRetrofit(retrofitType: RetrofitType): Retrofit {
        return Retrofit.Builder()
            .baseUrl(retrofitType.baseUrl)
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    fun getReversedGeocodingApi(retrofit: Retrofit): ReverseGeocodingAPI{
        return retrofit.create(ReverseGeocodingAPI::class.java)
    }
}