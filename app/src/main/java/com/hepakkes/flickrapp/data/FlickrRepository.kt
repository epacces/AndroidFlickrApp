package com.hepakkes.flickrapp.data

import com.hepakkes.flickrapp.data.api.RetrofitInstance
import com.hepakkes.flickrapp.data.model.FlickrPhoto

class FlickrRepository {

    private val apiService = RetrofitInstance.flickrApiService

    suspend fun getRecentPhotos(page: Int): Result<List<FlickrPhoto>> {
        return try {
            val response = apiService.getRecentPhotos(page = page)
            if (response.stat == "ok") {
                Result.success(response.photos.photoList)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
