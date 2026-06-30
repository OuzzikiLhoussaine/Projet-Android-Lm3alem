package com.lm3alem.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.ServiceRequest
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.repository.AuthRepository
import com.lm3alem.app.data.repository.RequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<RequestUiState>(RequestUiState.Idle)
    val uiState: State<RequestUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<RequestEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun sendRequest(
        artisanId: String,
        serviceName: String,
        description: String,
        address: String,
        budget: String,
        startTime: String,
        endTime: String,
        date: String
    ) {
        val clientId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = RequestUiState.Loading
            val request = ServiceRequest(
                clientId = clientId,
                artisanId = artisanId,
                serviceName = serviceName,
                description = description,
                address = address,
                budget = budget,
                startTime = startTime,
                endTime = endTime,
                status = RequestStatus.PENDING,
                date = date
            )
            val result = requestRepository.sendRequest(request)
            result.onSuccess {
                _uiState.value = RequestUiState.Success
                _eventFlow.emit(RequestEvent.RequestSent)
            }.onFailure {
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
                    val requestsWithClient = requests.map { request ->
                        async {
                            val client = authRepository.getUserDetails(request.clientId)
                            RequestWithUser(request, client)
                        }
                    }.awaitAll()
                    _uiState.value = RequestUiState.ArtisanRequestsLoaded(requestsWithClient)
                }
                .onFailure {
                    _uiState.value = RequestUiState.Error(it.message ?: "Failed to fetch requests")
                }
        }
    }

    fun fetchClientRequests() {
        val clientId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = RequestUiState.Loading
            requestRepository.getRequestsForClient(clientId)
                .onSuccess { requests ->
                    val requestsWithArtisan = requests.map { request ->
                        async {
                            val artisan = authRepository.getUserDetails(request.artisanId)
                            RequestWithUser(request, artisan)
                        }
                    }.awaitAll()
                    _uiState.value = RequestUiState.ClientRequestsLoaded(requestsWithArtisan)
                }
                .onFailure {
                    _uiState.value = RequestUiState.Error(it.message ?: "Failed to fetch requests")
                }
        }
    }

    fun updateStatus(requestId: String, status: RequestStatus) {
        viewModelScope.launch {
            requestRepository.updateRequestStatus(requestId, status)
                .onSuccess {
                    fetchRequests()
                }
        }
    }

    fun markAsReadByClient(requestId: String) {
        viewModelScope.launch {
            requestRepository.markRequestAsReadByClient(requestId)
        }
    }

    fun markAsReadByArtisan(requestId: String) {
        viewModelScope.launch {
            requestRepository.markRequestAsReadByArtisan(requestId)
        }
    }

    sealed class RequestUiState {
        object Idle : RequestUiState()
        object Loading : RequestUiState()
        object Success : RequestUiState()
        data class ArtisanRequestsLoaded(val requests: List<RequestWithUser>) : RequestUiState()
        data class ClientRequestsLoaded(val requests: List<RequestWithUser>) : RequestUiState()
        data class Error(val message: String) : RequestUiState()
    }

    sealed class RequestEvent {
        object RequestSent : RequestEvent()
    }
}

data class RequestWithUser(
    val request: ServiceRequest,
    val user: User?
)
