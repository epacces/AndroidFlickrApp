package com.hepakkes.flickrapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hepakkes.flickrapp.data.FlickrRepository
import com.hepakkes.flickrapp.data.model.FlickrPhoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PhotoGridUiState(
    val photos: List<FlickrPhoto> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val canLoadMore: Boolean = true
)

class PhotoGridViewModel : ViewModel() {

    private val repository = FlickrRepository()

    private val _uiState = MutableStateFlow(PhotoGridUiState())
    val uiState: StateFlow<PhotoGridUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getRecentPhotos(page = 1)
                .onSuccess { photos ->
                    _uiState.value = _uiState.value.copy(
                        photos = photos,
                        isLoading = false,
                        currentPage = 1,
                        canLoadMore = photos.isNotEmpty()
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun loadMorePhotos() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.canLoadMore) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoadingMore = true)

            val nextPage = currentState.currentPage + 1
            repository.getRecentPhotos(page = nextPage)
                .onSuccess { newPhotos ->
                    _uiState.value = _uiState.value.copy(
                        photos = currentState.photos + newPhotos,
                        isLoadingMore = false,
                        currentPage = nextPage,
                        canLoadMore = newPhotos.isNotEmpty()
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMore = false,
                        error = exception.message ?: "Failed to load more photos"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
