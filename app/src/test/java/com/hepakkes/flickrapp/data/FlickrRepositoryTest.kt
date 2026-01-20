package com.hepakkes.flickrapp.data

import com.hepakkes.flickrapp.data.api.FlickrApiService
import com.hepakkes.flickrapp.data.model.FlickrPhoto
import com.hepakkes.flickrapp.data.model.FlickrResponse
import com.hepakkes.flickrapp.data.model.PhotosWrapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class FlickrRepositoryTest {

    private lateinit var mockApiService: FlickrApiService
    private lateinit var repository: FlickrRepository

    @Before
    fun setup() {
        mockApiService = mockk()
        repository = FlickrRepository(mockApiService)
    }

    @Test
    fun `getRecentPhotos returns success when API returns ok status`() = runTest {
        val photos = listOf(
            FlickrPhoto("1", "owner1", "secret1", "server1", 1, "Title 1"),
            FlickrPhoto("2", "owner2", "secret2", "server2", 2, "Title 2")
        )
        val wrapper = PhotosWrapper(1, 10, 30, 300, photos)
        val response = FlickrResponse(wrapper, "ok")

        coEvery { mockApiService.getRecentPhotos(page = 1) } returns response

        val result = repository.getRecentPhotos(1)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.get(0)?.id)
        assertEquals("2", result.getOrNull()?.get(1)?.id)
    }

    @Test
    fun `getRecentPhotos returns failure when API returns error status`() = runTest {
        val wrapper = PhotosWrapper(0, 0, 0, 0, emptyList())
        val response = FlickrResponse(wrapper, "fail")

        coEvery { mockApiService.getRecentPhotos(page = 1) } returns response

        val result = repository.getRecentPhotos(1)

        assertTrue(result.isFailure)
        assertEquals("API returned error status", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getRecentPhotos returns failure when API throws exception`() = runTest {
        coEvery { mockApiService.getRecentPhotos(page = 1) } throws IOException("Network error")

        val result = repository.getRecentPhotos(1)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getRecentPhotos returns empty list when API returns no photos`() = runTest {
        val wrapper = PhotosWrapper(1, 0, 30, 0, emptyList())
        val response = FlickrResponse(wrapper, "ok")

        coEvery { mockApiService.getRecentPhotos(page = 1) } returns response

        val result = repository.getRecentPhotos(1)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `getRecentPhotos passes correct page number to API`() = runTest {
        val photos = listOf(FlickrPhoto("1", "owner1", "secret1", "server1", 1, "Title 1"))
        val wrapper = PhotosWrapper(5, 10, 30, 300, photos)
        val response = FlickrResponse(wrapper, "ok")

        coEvery { mockApiService.getRecentPhotos(page = 5) } returns response

        val result = repository.getRecentPhotos(5)

        assertTrue(result.isSuccess)
    }
}
