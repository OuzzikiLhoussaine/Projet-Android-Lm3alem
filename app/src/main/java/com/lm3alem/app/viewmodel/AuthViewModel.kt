package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val _eventFlow = MutableSharedFlow<AuthEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        val currentUser = authRepository.currentUser

        if (currentUser != null) {
            viewModelScope.launch {
                val details = authRepository.getUserDetails(currentUser.uid)

                if (details != null) {
                    _authState.value = AuthState.Success(details)
                    _eventFlow.emit(AuthEvent.NavigateToHome(details.role))
                }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authRepository.login(email, pass)

            result.onSuccess {
                val uid = authRepository.currentUser?.uid

                if (uid == null) {
                    _authState.value = AuthState.Error("User not found")
                    return@launch
                }

                val userDetails = authRepository.getUserDetails(uid)

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

    fun loginWithGoogle(credential: CustomCredential) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                val firebaseCredential = GoogleAuthProvider.getCredential(
                    googleIdTokenCredential.idToken,
                    null
                )

                FirebaseAuth.getInstance()
                    .signInWithCredential(firebaseCredential)
                    .await()

                val firebaseUser = FirebaseAuth.getInstance().currentUser

                if (firebaseUser == null) {
                    _authState.value = AuthState.Error("Google user not found")
                    return@launch
                }

                val uid = firebaseUser.uid

                val existingUser = authRepository.getUserDetails(uid)

                if (existingUser != null) {
                    _authState.value = AuthState.Success(existingUser)
                    _eventFlow.emit(AuthEvent.NavigateToHome(existingUser.role))
                } else {
                    val newUser = User(
                        fullName = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        phone = "",
                        city = "",
                        role = UserRole.CLIENT,
                        imageUrl = firebaseUser.photoUrl?.toString() ?: ""
                    )

                    authRepository.createUser(uid, newUser)

                    _authState.value = AuthState.Success(newUser)
                    _eventFlow.emit(AuthEvent.NavigateToRoleSelection)
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google login failed")
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        fullName: String,
        phone: String,
        city: String,
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val user = User(
                fullName = fullName,
                email = email,
                phone = phone,
                city = city,
                role = UserRole.CLIENT,
                imageUrl = imageUrl
            )

            val result = authRepository.register(email, pass, user)

            result.onSuccess {
                _authState.value = AuthState.Success(user)
                _eventFlow.emit(AuthEvent.NavigateToRoleSelection)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun selectRole(role: UserRole) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val uid = authRepository.currentUser?.uid

            if (uid != null) {
                val result = authRepository.updateUserRole(uid, role)

                result.onSuccess {
                    _eventFlow.emit(AuthEvent.NavigateToHome(role))
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Failed to update role")
                }
            } else {
                _authState.value = AuthState.Error("No authenticated user found")
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
        object NavigateToRoleSelection : AuthEvent()
    }
}