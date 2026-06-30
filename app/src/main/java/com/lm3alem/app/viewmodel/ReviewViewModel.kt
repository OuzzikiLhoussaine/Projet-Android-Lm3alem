package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.Review
import com.lm3alem.app.data.repository.AuthRepository
import com.lm3alem.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<ReviewUiState>(ReviewUiState.Idle)
    val uiState: State<ReviewUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<ReviewEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun addReview(artisanId: String, requestId: String, rating: Float, comment: String) {
        val clientId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            val review = Review(
                clientId = clientId,
                artisanId = artisanId,
                requestId = requestId,
                rating = rating,
                comment = comment,
                date = java.util.Date(),
                readByArtisan = false
            )
            reviewRepository.addReview(review)
                .onSuccess {
                    _uiState.value = ReviewUiState.Success
                    _eventFlow.emit(ReviewEvent.ReviewAdded)
                }
                .onFailure {
                    _uiState.value = ReviewUiState.Error(it.message ?: "Failed to add review")
                }
        }
    }

    fun fetchReviews(artisanId: String) {
        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            reviewRepository.getReviewsForArtisan(artisanId)
                .onSuccess { reviews ->
                    _uiState.value = ReviewUiState.ReviewsLoaded(reviews)
                }
                .onFailure {
                    _uiState.value = ReviewUiState.Error(it.message ?: "Failed to fetch reviews")
                }
        }
    }

    sealed class ReviewUiState {
        object Idle : ReviewUiState()
        object Loading : ReviewUiState()
        object Success : ReviewUiState()
        data class ReviewsLoaded(val reviews: List<Review>) : ReviewUiState()
        data class Error(val message: String) : ReviewUiState()
    }

    sealed class ReviewEvent {
        object ReviewAdded : ReviewEvent()
    }
}
