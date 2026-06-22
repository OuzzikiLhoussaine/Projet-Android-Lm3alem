package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.ArtisanWithUser
import com.lm3alem.app.data.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<ClientUiState>(ClientUiState.Loading)
    val uiState: State<ClientUiState> = _uiState

    private var allArtisans = listOf<ArtisanWithUser>()
    private var currentQuery = ""
    private var currentCategory = ""

    init {
        fetchArtisans()
    }

    fun fetchArtisans() {
        viewModelScope.launch {
            _uiState.value = ClientUiState.Loading
            clientRepository.getAllArtisans()
                .onSuccess { artisans ->
                    allArtisans = artisans
                    applyFilters()
                }
                .onFailure {
                    _uiState.value = ClientUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun filterArtisans(query: String, category: String) {
        currentQuery = query
        currentCategory = category
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = allArtisans.filter {
            val matchesQuery = it.artisan.job.contains(currentQuery, ignoreCase = true) || 
                               it.artisan.description.contains(currentQuery, ignoreCase = true) ||
                               it.user.fullName.contains(currentQuery, ignoreCase = true)
            
            val matchesCategory = currentCategory.isEmpty() || it.artisan.job.equals(currentCategory, ignoreCase = true)
            
            matchesQuery && matchesCategory
        }
        _uiState.value = ClientUiState.Success(filtered)
    }

    sealed class ClientUiState {
        object Loading : ClientUiState()
        data class Success(val artisans: List<ArtisanWithUser>) : ClientUiState()
        data class Error(val message: String) : ClientUiState()
    }
}
