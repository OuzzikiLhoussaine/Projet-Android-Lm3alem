package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val _eventFlow = MutableSharedFlow<AuthEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, pass)
            result.onSuccess {
                val userDetails = authRepository.currentUser?.uid?.let { authRepository.getUserDetails(it) }
                if (userDetails != null) {
                    _authState.value = AuthState.Success(userDetails)
                    _eventFlow.emit(AuthEvent.NavigateToHome(userDetails.role))
                } else {
                    _authState.value = AuthState.Error("User details not found")
                }
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, pass: String, fullName: String, phone: String, city: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = User(
                fullName = fullName,
                email = email,
                phone = phone,
                city = city,
                role = role
            )
            val result = authRepository.register(email, pass, user)
            result.onSuccess {
                _authState.value = AuthState.Success(user)
                _eventFlow.emit(AuthEvent.NavigateToHome(role))
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class AuthEvent {
        data class NavigateToHome(val role: UserRole) : AuthEvent()
    }
}
