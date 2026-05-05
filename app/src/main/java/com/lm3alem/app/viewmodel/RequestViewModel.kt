package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.ServiceRequest
import com.lm3alem.app.data.repository.AuthRepository
import com.lm3alem.app.data.repository.RequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf<RequestUiState>(RequestUiState.Idle)
    val uiState: State<RequestUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<RequestEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun sendRequest(artisanId: String, description: String) {
        val clientId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = RequestUiState.Loading
            val request = ServiceRequest(
                clientId = clientId,
                artisanId = artisanId,
                description = description,
                status = RequestStatus.PENDING
            )
            requestRepository.sendRequest(request)
                .onSuccess {
                    _uiState.value = RequestUiState.Success
                    _eventFlow.emit(RequestEvent.RequestSent)
                }
                .onFailure {
                    _uiState.value = RequestUiState.Error(it.message ?: "Failed to send request")
                }
        }
    }

    fun fetchRequests() {
        val artisanId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = RequestUiState.Loading
            requestRepository.getRequestsForArtisan(artisanId)
                .onSuccess { requests ->
                    _uiState.value = RequestUiState.RequestsLoaded(requests)
                }
                .onFailure {
                    _uiState.value = RequestUiState.Error(it.message ?: "Failed to fetch requests")
                }
        }
    }

    fun updateStatus(requestId: String, status: RequestStatus) {
        viewModelScope.launch {
            _uiState.value = RequestUiState.Loading
            requestRepository.updateRequestStatus(requestId, status)
                .onSuccess {
                    fetchRequests() // Refresh list
                }
                .onFailure {
                    _uiState.value = RequestUiState.Error(it.message ?: "Failed to update status")
                }
        }
    }

    sealed class RequestUiState {
        object Idle : RequestUiState()
        object Loading : RequestUiState()
        object Success : RequestUiState()
        data class RequestsLoaded(val requests: List<ServiceRequest>) : RequestUiState()
        data class Error(val message: String) : RequestUiState()
    }

    sealed class RequestEvent {
        object RequestSent : RequestEvent()
    }
}
