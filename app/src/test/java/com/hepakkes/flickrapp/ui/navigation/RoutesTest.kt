package com.hepakkes.flickrapp.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class RoutesTest {

    @Test
    fun `photoDetail creates correct route with all parameters`() {
        val route = Routes.photoDetail(
            photoId = "123",
            photoSecret = "abc",
            photoServer = "server1",
            photoTitle = "Test Photo"
        )

        assertTrue(route.startsWith("photo_detail/123/abc/server1/"))
        assertTrue(route.contains("Test"))
    }

    @Test
    fun `photoDetail encodes title with spaces`() {
        val route = Routes.photoDetail(
            photoId = "123",
            photoSecret = "abc",
            photoServer = "server1",
            photoTitle = "My Test Photo"
        )

        // URL encoded space is either + or %20
        assertTrue(route.contains("+") || route.contains("%20"))
    }

    @Test
    fun `photoDetail handles empty title`() {
        val route = Routes.photoDetail(
            photoId = "123",
            photoSecret = "abc",
            photoServer = "server1",
            photoTitle = ""
        )

        assertTrue(route.contains("Photo"))
    }

    @Test
    fun `photoDetail encodes special characters in title`() {
        val route = Routes.photoDetail(
            photoId = "123",
            photoSecret = "abc",
            photoServer = "server1",
            photoTitle = "Photo & Art"
        )

        // & should be encoded
        assertTrue(route.contains("%26") || !route.contains("&"))
    }

    @Test
    fun `photoDetail route can be decoded back`() {
        val originalTitle = "My Beautiful Photo"
        val route = Routes.photoDetail(
            photoId = "123",
            photoSecret = "abc",
            photoServer = "server1",
            photoTitle = originalTitle
        )

        val encodedTitle = route.substringAfterLast("/")
        val decodedTitle = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())

        assertEquals(originalTitle, decodedTitle)
    }

    @Test
    fun `PHOTO_GRID route is correct`() {
        assertEquals("photo_grid", Routes.PHOTO_GRID)
    }

    @Test
    fun `PHOTO_DETAIL route template contains all placeholders`() {
        assertTrue(Routes.PHOTO_DETAIL.contains("{photoId}"))
        assertTrue(Routes.PHOTO_DETAIL.contains("{photoSecret}"))
        assertTrue(Routes.PHOTO_DETAIL.contains("{photoServer}"))
        assertTrue(Routes.PHOTO_DETAIL.contains("{photoTitle}"))
    }
}
