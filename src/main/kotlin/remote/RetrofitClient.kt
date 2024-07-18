package remote

private const val  WEATHER_BASE_URL = "http://api.weatherapi.com/v1/"
private const val REVERSE_GEOCODER_BASE_URL = "https://nominatim.openstreetmap.org/"
private const val API_KEY = "2dbb071028094e9db1c165506241807"

class RetrofitClient {

    fun getClient(): Retrofit
}