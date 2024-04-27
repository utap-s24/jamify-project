package com.example.jamify.com.example.jamify


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.jamify.Data
import com.example.jamify.MyData
import com.example.jamify.SongInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongsRepository(private val api: APIInterface) {
    // NB: This is for our testing.
//    private val gson : Gson = GsonBuilder().registerTypeAdapter(
//        SpannableString::class.java, APIInterface.SpannableDeserializer()
//    ).create()

    private fun unpackPosts(response: APIInterface.ListingResponse): List<Data> {
        // XXX Write me.
        return response.data.children.map {value -> value.data}
    }
    private fun unpackSongs(response: APIInterface.SongResponse): SongInfo {
        // XXX Write me.
        return response.data
    }


    suspend fun retrieveSongInfo(id: Long) : SongInfo? {
        Log.d(javaClass.simpleName, id.toString())

        val response = api.getSong(id)

        Log.d(javaClass.simpleName, "retrieveSongInfo: " + response)
//        var responseBody : SongInfo? = null
//        response.enqueue(object : Callback<SongInfo?> {
//            override fun onResponse(call: Call<SongInfo?>, response: Response<SongInfo?>) {
//                responseBody = response.body()!!
//                Log.d("TAG: onResponse", "onResponse: "  + responseBody.toString())
//
//            }
//
//
//            override fun onFailure(p0: Call<SongInfo?>, p1: Throwable) {
//                Log.e("MainActivity", "onFailure: " )
//            }
//        })
        return response
    }

    suspend fun retrieveSearchedSongs(searchTerm: String): List<Data>?{
        val response = api.getData(searchTerm)
//        var responseBody : List<Data>? = null

//        response.enqueue(object : Callback<MyData?> {
//            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
//                responseBody = response.body()?.data!!
//                Log.d("TAG: onResponse", "onResponse: "  + responseBody.toString())
//
//            }
//
//
//            override fun onFailure(p0: Call<MyData?>, p1: Throwable) {
//                Log.e("MainActivity", "onFailure: ")
//            }
//        })
        return response.data
    }
}