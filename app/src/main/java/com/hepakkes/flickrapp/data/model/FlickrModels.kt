package com.hepakkes.flickrapp.data.model

import com.google.gson.annotations.SerializedName

data class FlickrResponse(
    @SerializedName("photos") val photos: PhotosWrapper,
    @SerializedName("stat") val stat: String
)

data class PhotosWrapper(
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("perpage") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("photo") val photoList: List<FlickrPhoto>
)

data class FlickrPhoto(
    @SerializedName("id") val id: String,
    @SerializedName("owner") val owner: String,
    @SerializedName("secret") val secret: String,
    @SerializedName("server") val server: String,
    @SerializedName("farm") val farm: Int,
    @SerializedName("title") val title: String
) {
    fun getPhotoUrl(size: String = "q"): String {
        // Size options: s (small square 75x75), q (large square 150x150),
        // t (thumbnail), m (small), n (small 320), z (medium 640), c (medium 800), b (large)
        return "https://live.staticflickr.com/${server}/${id}_${secret}_${size}.jpg"
    }
}
