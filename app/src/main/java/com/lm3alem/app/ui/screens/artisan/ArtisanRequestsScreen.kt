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
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.RequestCard
import com.lm3alem.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanRequestsScreen(
    navController: NavHostController,
    viewModel: RequestViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.fetchRequests()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.service_requests),
                onBackClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is RequestViewModel.RequestUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is RequestViewModel.RequestUiState.RequestsLoaded -> {
                    if (state.requests.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_requests_yet),
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                        ) {
                            items(state.requests) { request ->
                                RequestCard(request) { status ->
                                    viewModel.updateStatus(request.id, status)
                                }
                            }
                        }
                    }
                }
                is RequestViewModel.RequestUiState.Error -> {
                    ErrorMessage(
                        message = stringResource(R.string.error_message, state.message),
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    )
                }
                else -> {}
            }
        }
    }
}
