package com.example.marsphotos.network

import com.example.marsphotos.model.MarsPhoto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

interface MarsApiService {
    @GET("photos")
    suspend fun getPhotos(): List<MarsPhoto>
}

object RetrofitClient {
    private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com"

    private val jsonConverterFactory by lazy {
        Json.asConverterFactory("application/json".toMediaType())
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonConverterFactory)
            .build()
    }
}


object MarsApi {
    val service: MarsApiService by lazy {
        RetrofitClient.retrofit.create(MarsApiService::class.java)
    }
}
