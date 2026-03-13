package com.firstaidnow.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {

    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyHospitals(
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "hospital",
        @Query("key") apiKey: String
    ): PlacesResponse
}

data class PlacesResponse(
    val results: List<PlaceResult>,
    val status: String,
    val error_message: String? = null
)

data class PlaceResult(
    val name: String,
    val vicinity: String?,
    val geometry: Geometry,
    val place_id: String,
    val rating: Double?
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
