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

    init {
        fetchArtisans()
    }

    fun fetchArtisans() {
        viewModelScope.launch {
            _uiState.value = ClientUiState.Loading
            clientRepository.getAllArtisans()
                .onSuccess { artisans ->
                    allArtisans = artisans
                    _uiState.value = ClientUiState.Success(artisans)
                }
                .onFailure {
                    _uiState.value = ClientUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun filterArtisans(query: String, city: String) {
        val filtered = allArtisans.filter {
            (it.artisan.job.contains(query, ignoreCase = true) || it.artisan.description.contains(query, ignoreCase = true)) &&
            (city.isEmpty() || it.artisan.city.equals(city, ignoreCase = true))
        }
        _uiState.value = ClientUiState.Success(filtered)
    }

    sealed class ClientUiState {
        object Loading : ClientUiState()
        data class Success(val artisans: List<ArtisanWithUser>) : ClientUiState()
        data class Error(val message: String) : ClientUiState()
    }
}
