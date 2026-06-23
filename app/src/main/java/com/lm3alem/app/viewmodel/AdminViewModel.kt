package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _usersState = mutableStateOf<AdminState>(AdminState.Loading)
    val usersState: State<AdminState> = _usersState

    private var allUsers: List<User> = emptyList()

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _selectedRole = mutableStateOf<UserRole?>(null)
    val selectedRole: State<UserRole?> = _selectedRole

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = AdminState.Loading
            val result = authRepository.getAllUsers()
            result.onSuccess {
                allUsers = it
                applyFilters()
            }.onFailure {
                _usersState.value = AdminState.Error(it.message ?: "Failed to load users")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun onRoleFilterChange(role: UserRole?) {
        _selectedRole.value = role
        applyFilters()
    }

    private fun applyFilters() {
        val filteredUsers = allUsers.filter { user ->
            val matchesSearch = user.fullName.contains(_searchQuery.value, ignoreCase = true) ||
                    user.email.contains(_searchQuery.value, ignoreCase = true)
            val matchesRole = _selectedRole.value == null || user.userRole == _selectedRole.value
            matchesSearch && matchesRole
        }
        _usersState.value = AdminState.Success(filteredUsers)
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val result = authRepository.deleteUser(userId)
            result.onSuccess {
                loadUsers()
            }.onFailure {
                // Handle error
            }
        }
    }

    sealed class AdminState {
        object Loading : AdminState()
        data class Success(val users: List<User>) : AdminState()
        data class Error(val message: String) : AdminState()
    }
}
