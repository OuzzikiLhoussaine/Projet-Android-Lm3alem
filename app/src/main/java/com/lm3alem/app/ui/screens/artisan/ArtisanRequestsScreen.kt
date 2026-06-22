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
import com.lm3alem.app.ui.components.ArtisanBottomBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.RequestCard
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.RequestViewModel
import com.lm3alem.app.viewmodel.ProfileViewModel
import com.lm3alem.app.viewmodel.ChatViewModel
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.RequestStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanRequestsScreen(
    navController: NavHostController,
    viewModel: RequestViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.fetchRequests()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.service_requests),
                onBackClick = { navController.popBackStack() },
            )
        },
        bottomBar = {
            ArtisanBottomBar(navController = navController, currentRoute = Screen.ArtisanRequests.route)
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
                                var clientInfo by remember { mutableStateOf<User?>(null) }
                                
                                LaunchedEffect(request.clientId) {
                                    clientInfo = profileViewModel.getUserById(request.clientId)
                                }

                                RequestCard(
                                    request = request,
                                    client = clientInfo
                                ) { status ->
                                    viewModel.updateStatus(request.id, status)
                                    
                                    // If accepted, navigate to chat
                                    if (status == RequestStatus.ACCEPTED) {
                                        scope.launch {
                                            val roomId = chatViewModel.getOrCreateChatRoom(request.clientId)
                                            navController.navigate(Screen.ChatDetail.createRoute(roomId, request.clientId))
                                        }
                                    }
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
