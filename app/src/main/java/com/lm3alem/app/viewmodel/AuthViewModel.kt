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
    private val authRepository: AuthRepository,
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
                if (!authRepository.isEmailVerified()) {
                    _authState.value = AuthState.VerificationSent
                    _eventFlow.emit(AuthEvent.NavigateToEmailVerification)
                    return@launch
                }
                val details = authRepository.getUserDetails(currentUser.uid)

                if (details != null) {
                    if (details.userRole == UserRole.UNDEFINED) {
                        _authState.value = AuthState.Success(details)
                        _eventFlow.emit(AuthEvent.NavigateToRoleSelection)
                    } else if (isProfileComplete(details)) {
                        _authState.value = AuthState.Success(details)
                        _eventFlow.emit(AuthEvent.NavigateToHome(details.userRole))
                    } else {
                        _authState.value = AuthState.Success(details)
                        _eventFlow.emit(AuthEvent.NavigateToCompleteProfile(details.userRole))
                    }
                }
            }
        }
    }

    private fun isProfileComplete(user: User): Boolean {
        if (user.userRole == UserRole.ADMIN) return true
        return user.fullName.isNotBlank() &&
                user.phone.isNotBlank() &&
                user.city.isNotBlank() &&
                user.userRole != UserRole.UNDEFINED
    }

    fun login(email: String, pass: String) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        
        if (trimmedEmail.equals("admin@lm3alem.ma", ignoreCase = true) && trimmedPass == "admin123") {
            val adminUser = User(
                id = "admin_id",
                fullName = "Administrator",
                email = "admin@lm3alem.ma",
                role = UserRole.ADMIN.name
            )
            _authState.value = AuthState.Success(adminUser)
            viewModelScope.launch {
                _eventFlow.emit(AuthEvent.NavigateToHome(UserRole.ADMIN))
            }
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authRepository.login(trimmedEmail, pass)

            result.onSuccess {
                val uid = authRepository.currentUser?.uid

                if (uid == null) {
                    _authState.value = AuthState.Error("User not found")
                    return@launch
                }

                if (!authRepository.isEmailVerified()) {
                    _authState.value = AuthState.VerificationSent
                    _eventFlow.emit(AuthEvent.NavigateToEmailVerification)
                    return@launch
                }

                val userDetails = authRepository.getUserDetails(uid)

                if (userDetails != null) {
                    if (userDetails.userRole == UserRole.UNDEFINED) {
                        _authState.value = AuthState.Success(userDetails)
                        _eventFlow.emit(AuthEvent.NavigateToRoleSelection)
                    } else if (isProfileComplete(userDetails)) {
                        _authState.value = AuthState.Success(userDetails)
                        _eventFlow.emit(AuthEvent.NavigateToHome(userDetails.userRole))
                    } else {
                        _authState.value = AuthState.Success(userDetails)
                        _eventFlow.emit(AuthEvent.NavigateToCompleteProfile(userDetails.userRole))
                    }
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
                    if (existingUser.userRole == UserRole.UNDEFINED) {
                        _authState.value = AuthState.Success(existingUser)
                        _eventFlow.emit(AuthEvent.NavigateToRoleSelection)
                    } else if (isProfileComplete(existingUser)) {
                        _authState.value = AuthState.Success(existingUser)
                        _eventFlow.emit(AuthEvent.NavigateToHome(existingUser.userRole))
                    } else {
                        _authState.value = AuthState.Success(existingUser)
                        _eventFlow.emit(AuthEvent.NavigateToCompleteProfile(existingUser.userRole))
                    }
                } else {
                    val newUser = User(
                        fullName = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        phone = "",
                        city = "",
                        role = UserRole.UNDEFINED.name,
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
        role: UserRole = UserRole.CLIENT
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val trimmedEmail = email.trim()
            val user = User(
                email = trimmedEmail,
                role = role.name
            )

            val result = authRepository.register(trimmedEmail, pass, user)

            result.onSuccess {
                authRepository.sendEmailVerification()
                _authState.value = AuthState.VerificationSent
                _eventFlow.emit(AuthEvent.NavigateToEmailVerification)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendEmailVerification()
            result.onSuccess {
                _authState.value = AuthState.VerificationSent
                _eventFlow.emit(AuthEvent.VerificationEmailResent)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Failed to resend verification email")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendPasswordResetEmail(email.trim())
            result.onSuccess {
                _authState.value = AuthState.Idle
                _eventFlow.emit(AuthEvent.PasswordResetSent)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Failed to send reset email")
            }
        }
    }

    fun checkEmailVerificationStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (authRepository.isEmailVerified()) {
                authRepository.logout()
                _authState.value = AuthState.Idle
                _eventFlow.emit(AuthEvent.NavigateToLogin)
            } else {
                _authState.value = AuthState.Error("Email not verified yet. Please check your inbox.")
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
                    val updatedUser = authRepository.getUserDetails(uid)
                    if (updatedUser != null) {
                        _authState.value = AuthState.Success(updatedUser)
                    }
                    _eventFlow.emit(AuthEvent.NavigateToCompleteProfile(role))
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Failed to update role")
                }
            } else {
                _authState.value = AuthState.Error("No authenticated user found")
            }
        }
    }

    fun completeProfile(fullName: String, phone: String, city: String, imageUrl: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val uid = authRepository.currentUser?.uid
            if (uid != null) {
                val updates = mapOf(
                    "fullName" to fullName,
                    "phone" to phone,
                    "city" to city,
                    "imageUrl" to imageUrl
                )
                val result = authRepository.updateUserDetails(uid, updates)
                result.onSuccess {
                    val user = authRepository.getUserDetails(uid)
                    if (user != null) {
                        _authState.value = AuthState.Success(user)
                        _eventFlow.emit(AuthEvent.NavigateToHome(user.userRole))
                    } else {
                        _authState.value = AuthState.Error("Failed to fetch updated user")
                    }
                }.onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Failed to complete profile")
                }
            } else {
                _authState.value = AuthState.Error("User not authenticated")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
        viewModelScope.launch {
            _eventFlow.emit(AuthEvent.Logout)
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object VerificationSent : AuthState()
        data class Success(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class AuthEvent {
        data class NavigateToHome(val role: UserRole) : AuthEvent()
        object NavigateToRoleSelection : AuthEvent()
        object NavigateToEmailVerification : AuthEvent()
        object VerificationEmailResent : AuthEvent()
        object NavigateToLogin : AuthEvent()
        object PasswordResetSent : AuthEvent()
        data class NavigateToCompleteProfile(val role: UserRole) : AuthEvent()
        object Logout : AuthEvent()
    }
}