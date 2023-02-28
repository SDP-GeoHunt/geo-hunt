package com.github.geohunt.app.api

import retrofit2.Call
import retrofit2.http.GET

/**
 * Represents the server instance of the Bored API.
 */
interface BoredApi {
    @GET("activity")
    fun getActivity(): Call<BoredActivityData>
}