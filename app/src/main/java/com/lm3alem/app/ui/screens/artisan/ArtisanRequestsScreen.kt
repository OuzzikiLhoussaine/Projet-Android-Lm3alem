package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.ServiceRequest
import com.lm3alem.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanRequestsScreen(
    navController: NavHostController,
    viewModel: RequestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.fetchRequests()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Service Requests") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is RequestViewModel.RequestUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RequestViewModel.RequestUiState.RequestsLoaded -> {
                    if (state.requests.isEmpty()) {
                        Text(text = "No requests yet", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.requests) { request ->
                                RequestCard(request, onStatusUpdate = { status ->
                                    viewModel.updateStatus(request.id, status)
                                })
                            }
                        }
                    }
                }
                is RequestViewModel.RequestUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                else -> {}
            }
        }
    }
}

@Composable
fun RequestCard(request: ServiceRequest, onStatusUpdate: (RequestStatus) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Status: ${request.status}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = request.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (request.status == RequestStatus.PENDING) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onStatusUpdate(RequestStatus.REFUSED) }) {
                        Text("Refuse", color = MaterialTheme.colorScheme.error)
                    }
                    Button(onClick = { onStatusUpdate(RequestStatus.ACCEPTED) }) {
                        Text("Accept")
                    }
                }
            } else if (request.status == RequestStatus.ACCEPTED) {
                Button(
                    onClick = { onStatusUpdate(RequestStatus.DONE) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Mark as Done")
                }
            }
        }
    }
}
