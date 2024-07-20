package api

import kotlinx.coroutines.Deferred
import data.remote.models.ReversedCountry
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingAPI {

    @GET("reverse")
    fun getCountryNameByCoordinates(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("format") formatData: String,
    ): Deferred<ReversedCountry>


}