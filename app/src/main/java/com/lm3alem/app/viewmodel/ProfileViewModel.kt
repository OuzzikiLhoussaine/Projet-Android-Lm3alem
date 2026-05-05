package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<ProfileUiState>(ProfileUiState.Idle)
    val uiState: State<ProfileUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<ProfileEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun fetchUserProfile() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _uiState.value = ProfileUiState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val user = authRepository.getUserDetails(uid)
            if (user != null) {
                _uiState.value = ProfileUiState.Success(user)
            } else {
                _uiState.value = ProfileUiState.Error("User details not found")
            }
        }
    }

    fun updateProfile(fullName: String, phone: String, city: String, imageUrl: String) {
        val uid = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val updates = mapOf(
                "fullName" to fullName,
                "phone" to phone,
                "city" to city,
                "imageUrl" to imageUrl
            )
            val result = authRepository.updateUserDetails(uid, updates)
            result.onSuccess {
                _eventFlow.emit(ProfileEvent.ProfileUpdated)
                fetchUserProfile()
            }.onFailure {
                _uiState.value = ProfileUiState.Error(it.message ?: "Failed to update profile")
            }
        }
    }

    sealed class ProfileUiState {
        object Idle : ProfileUiState()
        object Loading : ProfileUiState()
        data class Success(val user: User) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
    }

    sealed class ProfileEvent {
        object ProfileUpdated : ProfileEvent()
    }
}
