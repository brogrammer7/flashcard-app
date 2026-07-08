package com.restest.flashcards.network

import com.restest.flashcards.data.model.CardResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://resflash.free.beeceptor.com/v1/"

private class ApiInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
            .newBuilder()
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}

private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .addInterceptor(ApiInterceptor())
    .addInterceptor(logging)
    .build()

private val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface ResFlashCardApiService {
    @GET("flashcards")
    suspend fun getFlashCards(): Response<CardResponse>
}

object ResApi {
    val retrofitService: ResFlashCardApiService by lazy {
        retrofit.create(ResFlashCardApiService::class.java)
    }
}