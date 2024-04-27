package com.example.jamify.com.example.jamify

import android.text.SpannableString
import com.example.jamify.Data
import com.example.jamify.MyData
import com.example.jamify.SongInfo
import com.example.jamify.model.PostMeta
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface APIInterface {
    @Headers(
        "X-RapidAPI-Key: c39654d276msh319f1cf0ea870b3p149c6ajsn76070a95093e",
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com"
    )
    @GET("search")
    suspend fun getData(@Query("q") query: String): MyData
    @Headers(
        "X-RapidAPI-Key: c39654d276msh319f1cf0ea870b3p149c6ajsn76070a95093e",
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com"
    )
    @GET("track/{query}")
    suspend fun getSong(@Path("query") query: Long): SongInfo


// NB: Everything below here is fine, no need to change it
    class SongResponse(val data: SongInfo)


    // https://www.reddit.com/dev/api/#listings
    class ListingResponse(val data: ListingData)

    class ListingData(
        val children: List<PostChildrenResponse>,
        val after: String?,
        val before: String?
    )

    data class PostChildrenResponse(val data: Data)

    // This class allows Retrofit to parse items in our model of type
    // SpannableString.  Note, given the amount of "work" we do to
    // enable this behavior, one can argue that Retrofit is a bit...."simple."
    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        // @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }
    companion object {
        // Tell Gson to use our SpannableString deserializer
//        private fun buildGsonConverterFactory(): GsonConverterFactory {
//            val gsonBuilder = GsonBuilder().registerTypeAdapter(
//                SpannableString::class.java, SpannableDeserializer()
//            )
//            return GsonConverterFactory.create(gsonBuilder.create())
//        }

        // Keep the base URL simple
        //private const val BASE_URL = "https://deezerdevs-deezer.p.rapidapi.com"
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("deezerdevs-deezer.p.rapidapi.com")
            .build()

        fun create(): APIInterface  {
//            val client = OkHttpClient.Builder()
//            // Add other Interceptors
//
//
//                .addInterceptor(HttpLoggingInterceptor().apply {
//                    // Enable basic HTTP logging to help with debugging.
//                    this.level = HttpLoggingInterceptor.Level.BASIC
//                })
//                .build()
            //        val retrofitBuilder = Retrofit.Builder()
//            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(APIInterface::class.java)
//
//        val retrofitData = retrofitBuilder.getData("eminem")
//
//        retrofitData.enqueue(object : Callback<MyData?> {
//            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
//                val responseBody = response.body()?.data!!
//                myAdapter = DataAdapter(activity!!, responseBody)
//                recyclerView.adapter = myAdapter
//                recyclerView.layoutManager = LinearLayoutManager(context)
////                val textView = binding.response
////                textView.text = responseBody.toString()
//                Log.d("TAG: onResponse", "onResponse: "  + responseBody.toString())
//
//            }
//
//            override fun onFailure(call: Call<MyData?>, t: Throwable) {
//                // if api call is failure then this method is executed
//                Log.e("MainActivity", "onFailure: " + t.message)
//            }
//        })
            return Retrofit.Builder()
                .baseUrl("https://deezerdevs-deezer.p.rapidapi.com")
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(APIInterface::class.java)
        }
    }
}