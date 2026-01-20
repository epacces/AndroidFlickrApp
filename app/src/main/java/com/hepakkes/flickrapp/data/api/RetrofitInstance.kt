package com.hepakkes.flickrapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FlickrApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val flickrApiService: FlickrApiService by lazy {
        retrofit.create(FlickrApiService::class.java)
    }
}
