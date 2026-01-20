package com.hepakkes.flickrapp.ui.screens

import app.cash.turbine.test
import com.hepakkes.flickrapp.data.FlickrRepository
import com.hepakkes.flickrapp.data.model.FlickrPhoto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoGridViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: FlickrRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestPhotos(count: Int): List<FlickrPhoto> {
        return (1..count).map { i ->
            FlickrPhoto("$i", "owner$i", "secret$i", "server$i", i, "Title $i")
        }
    }

    @Test
    fun `initial state has empty photos before loading`() = runTest {
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(emptyList())

        val viewModel = PhotoGridViewModel(mockRepository)

        // Before advancing the dispatcher, isLoading should be true
        val initialState = viewModel.uiState.value
        assertTrue(initialState.photos.isEmpty())
        assertEquals(0, initialState.currentPage)

        // After loading completes
        testDispatcher.scheduler.advanceUntilIdle()

        val loadedState = viewModel.uiState.value
        assertFalse(loadedState.isLoading)
    }

    @Test
    fun `loadPhotos updates state with photos on success`() = runTest {
        val photos = createTestPhotos(3)
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(photos)

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.photos.size)
            assertEquals("1", state.photos[0].id)
            assertFalse(state.isLoading)
            assertEquals(1, state.currentPage)
            assertTrue(state.canLoadMore)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadPhotos updates state with error on failure`() = runTest {
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.failure(Exception("Network error"))

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
            assertTrue(state.photos.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMorePhotos appends photos to existing list`() = runTest {
        val firstPagePhotos = createTestPhotos(3)
        val secondPagePhotos = (4..6).map { i ->
            FlickrPhoto("$i", "owner$i", "secret$i", "server$i", i, "Title $i")
        }

        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(firstPagePhotos)
        coEvery { mockRepository.getRecentPhotos(page = 2) } returns Result.success(secondPagePhotos)

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMorePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(6, state.photos.size)
            assertEquals("1", state.photos[0].id)
            assertEquals("6", state.photos[5].id)
            assertEquals(2, state.currentPage)
            assertFalse(state.isLoadingMore)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMorePhotos sets canLoadMore to false when empty list returned`() = runTest {
        val firstPagePhotos = createTestPhotos(3)

        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(firstPagePhotos)
        coEvery { mockRepository.getRecentPhotos(page = 2) } returns Result.success(emptyList())

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMorePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.canLoadMore)
            assertEquals(3, state.photos.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMorePhotos does not load when canLoadMore is false`() = runTest {
        val photos = createTestPhotos(3)
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(photos)
        coEvery { mockRepository.getRecentPhotos(page = 2) } returns Result.success(emptyList())

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // First load more - returns empty, sets canLoadMore = false
        viewModel.loadMorePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Second load more - should not call API since canLoadMore is false
        viewModel.loadMorePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify page 2 was only called once
        coVerify(exactly = 1) { mockRepository.getRecentPhotos(page = 2) }
    }

    @Test
    fun `clearError clears the error state`() = runTest {
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.failure(Exception("Error"))

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadPhotos sets canLoadMore to false when empty list returned`() = runTest {
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(emptyList())

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.canLoadMore)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadPhotos does not load when already loading`() = runTest {
        val photos = createTestPhotos(3)
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(photos)

        val viewModel = PhotoGridViewModel(mockRepository)

        // At this point init has called loadPhotos which set isLoading = true
        // The coroutine hasn't completed yet because we haven't advanced the dispatcher
        assertTrue(viewModel.uiState.value.isLoading)

        // These calls should be ignored because isLoading is true
        viewModel.loadPhotos()
        viewModel.loadPhotos()

        testDispatcher.scheduler.advanceUntilIdle()

        // Should only have called the API once (from init)
        coVerify(exactly = 1) { mockRepository.getRecentPhotos(page = 1) }
    }

    @Test
    fun `loadMorePhotos handles error correctly`() = runTest {
        val photos = createTestPhotos(3)
        coEvery { mockRepository.getRecentPhotos(page = 1) } returns Result.success(photos)
        coEvery { mockRepository.getRecentPhotos(page = 2) } returns Result.failure(Exception("Load more failed"))

        val viewModel = PhotoGridViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMorePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Load more failed", state.error)
            assertFalse(state.isLoadingMore)
            // Original photos should still be there
            assertEquals(3, state.photos.size)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
