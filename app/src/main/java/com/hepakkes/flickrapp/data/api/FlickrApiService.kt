package com.hepakkes.flickrapp.data.api

import com.hepakkes.flickrapp.data.model.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApiService {

    @GET("services/rest/")
    suspend fun getRecentPhotos(
        @Query("method") method: String = "flickr.photos.getRecent",
        @Query("api_key") apiKey: String = API_KEY,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): FlickrResponse

    companion object {
        const val BASE_URL = "https://api.flickr.com/"
        // Get your API key from https://www.flickr.com/services/apps/create/
        const val API_KEY = "cd5aae345dc9c59de0f288ec9dca47c3"
    }
}
