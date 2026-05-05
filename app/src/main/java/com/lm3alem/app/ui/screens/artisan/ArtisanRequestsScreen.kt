package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
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
        topBar = { TopAppBar(title = { Text(stringResource(R.string.service_requests)) }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is RequestViewModel.RequestUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RequestViewModel.RequestUiState.RequestsLoaded -> {
                    if (state.requests.isEmpty()) {
                        Text(text = stringResource(R.string.no_requests_yet), modifier = Modifier.align(Alignment.Center))
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
                    Text(text = stringResource(R.string.error_message, state.message), color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
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
            val statusText = when (request.status) {
                RequestStatus.PENDING -> stringResource(R.string.status_pending)
                RequestStatus.ACCEPTED -> stringResource(R.string.status_accepted)
                RequestStatus.REFUSED -> stringResource(R.string.status_refused)
                RequestStatus.DONE -> stringResource(R.string.status_done)
            }
            Text(text = stringResource(R.string.status_label, statusText), style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = request.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (request.status == RequestStatus.PENDING) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onStatusUpdate(RequestStatus.REFUSED) }) {
                        Text(stringResource(R.string.refuse), color = MaterialTheme.colorScheme.error)
                    }
                    Button(onClick = { onStatusUpdate(RequestStatus.ACCEPTED) }) {
                        Text(stringResource(R.string.accept))
                    }
                }
            } else if (request.status == RequestStatus.ACCEPTED) {
                Button(
                    onClick = { onStatusUpdate(RequestStatus.DONE) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.mark_as_done))
                }
            }
        }
    }
}
