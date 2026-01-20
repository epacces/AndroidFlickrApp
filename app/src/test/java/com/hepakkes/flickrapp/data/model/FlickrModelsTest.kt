package com.hepakkes.flickrapp.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FlickrModelsTest {

    @Test
    fun `FlickrPhoto getPhotoUrl returns correct URL with default size`() {
        val photo = FlickrPhoto(
            id = "12345",
            owner = "owner123",
            secret = "abc123",
            server = "server1",
            farm = 1,
            title = "Test Photo"
        )

        val expectedUrl = "https://live.staticflickr.com/server1/12345_abc123_q.jpg"
        assertEquals(expectedUrl, photo.getPhotoUrl())
    }

    @Test
    fun `FlickrPhoto getPhotoUrl returns correct URL with custom size`() {
        val photo = FlickrPhoto(
            id = "12345",
            owner = "owner123",
            secret = "abc123",
            server = "server1",
            farm = 1,
            title = "Test Photo"
        )

        val expectedUrl = "https://live.staticflickr.com/server1/12345_abc123_b.jpg"
        assertEquals(expectedUrl, photo.getPhotoUrl("b"))
    }

    @Test
    fun `FlickrPhoto getPhotoUrl returns correct URL with small square size`() {
        val photo = FlickrPhoto(
            id = "99999",
            owner = "testowner",
            secret = "secretkey",
            server = "65535",
            farm = 66,
            title = "Another Photo"
        )

        val expectedUrl = "https://live.staticflickr.com/65535/99999_secretkey_s.jpg"
        assertEquals(expectedUrl, photo.getPhotoUrl("s"))
    }

    @Test
    fun `PhotosWrapper contains correct data`() {
        val photos = listOf(
            FlickrPhoto("1", "owner1", "secret1", "server1", 1, "Title 1"),
            FlickrPhoto("2", "owner2", "secret2", "server2", 2, "Title 2")
        )

        val wrapper = PhotosWrapper(
            page = 1,
            pages = 10,
            perPage = 30,
            total = 300,
            photoList = photos
        )

        assertEquals(1, wrapper.page)
        assertEquals(10, wrapper.pages)
        assertEquals(30, wrapper.perPage)
        assertEquals(300, wrapper.total)
        assertEquals(2, wrapper.photoList.size)
        assertEquals("1", wrapper.photoList[0].id)
        assertEquals("2", wrapper.photoList[1].id)
    }

    @Test
    fun `FlickrResponse contains correct data`() {
        val photos = listOf(
            FlickrPhoto("1", "owner1", "secret1", "server1", 1, "Title 1")
        )
        val wrapper = PhotosWrapper(1, 10, 30, 300, photos)

        val response = FlickrResponse(
            photos = wrapper,
            stat = "ok"
        )

        assertEquals("ok", response.stat)
        assertEquals(1, response.photos.page)
        assertEquals(1, response.photos.photoList.size)
    }
}
