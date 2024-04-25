package com.example.jamify.com.example.jamify

import com.example.jamify.MyData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface APIInterface {
    @Headers("X-RapidAPI-Key: c39654d276msh319f1cf0ea870b3p149c6ajsn76070a95093e",
            "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com")
    @GET("search")
    fun getData(@Query("q") query: String): Call<MyData>
}