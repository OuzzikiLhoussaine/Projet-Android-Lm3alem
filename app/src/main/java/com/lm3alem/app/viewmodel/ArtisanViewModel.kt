package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.data.model.ArtisanWithUser
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.repository.ArtisanRepository
import com.lm3alem.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtisanViewModel @Inject constructor(
    private val artisanRepository: ArtisanRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf<ArtisanUiState>(ArtisanUiState.Idle)
    val uiState: State<ArtisanUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<ArtisanEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _artisanWithUser = mutableStateOf<ArtisanWithUser?>(null)
    val artisanWithUser: State<ArtisanWithUser?> = _artisanWithUser

    init {
        authRepository.currentUser?.uid?.let { fetchArtisanProfile(it) }
    }

    fun fetchArtisanProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = ArtisanUiState.Loading
            val profile = artisanRepository.getArtisanProfile(userId)
            val user = authRepository.getUserDetails(userId)
            
            if (user != null) {
                if (profile != null) {
                    _artisanWithUser.value = ArtisanWithUser(profile, user)
                }
                _uiState.value = ArtisanUiState.Idle
            } else {
                _uiState.value = ArtisanUiState.Error("User details not found")
            }
        }
    }

    fun saveProfile(
        job: String,
        description: String,
        experience: String,
        price: String,
    ) {
        viewModelScope.launch {
            _uiState.value = ArtisanUiState.Loading
            
            val userId = authRepository.currentUser?.uid
            if (userId == null) {
                _uiState.value = ArtisanUiState.Error("User not authenticated. Please log in again.")
                return@launch
            }

            try {
                val userDetails = authRepository.getUserDetails(userId)
                val currentProfile = artisanRepository.getArtisanProfile(userId)
                val profile = ArtisanProfile(
                    userId = userId,
                    job = job,
                    description = description,
                    experience = experience.toIntOrNull() ?: 0,
                    city = userDetails?.city ?: "",
                    price = price.toDoubleOrNull() ?: 0.0,
                    rating = currentProfile?.rating ?: 0.0,
                    reviewCount = currentProfile?.reviewCount ?: 0
                )
                
                val result = artisanRepository.saveArtisanProfile(profile)
                result.onSuccess {
                    _uiState.value = ArtisanUiState.Success
                    _eventFlow.emit(ArtisanEvent.ProfileSaved)
                }.onFailure {
                    _uiState.value = ArtisanUiState.Error(it.message ?: "Failed to save profile")
                }
            } catch (e: Exception) {
                _uiState.value = ArtisanUiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    sealed class ArtisanUiState {
        object Idle : ArtisanUiState()
        object Loading : ArtisanUiState()
        object Success : ArtisanUiState()
        data class Error(val message: String) : ArtisanUiState()
    }

    sealed class ArtisanEvent {
        object ProfileSaved : ArtisanEvent()
    }
}
