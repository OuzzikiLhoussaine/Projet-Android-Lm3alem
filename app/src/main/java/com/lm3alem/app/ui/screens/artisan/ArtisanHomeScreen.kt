package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ArtisanBottomBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.components.RequestCard
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel
import com.lm3alem.app.viewmodel.RequestViewModel
import com.lm3alem.app.viewmodel.ProfileViewModel
import com.lm3alem.app.viewmodel.ChatViewModel
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.RequestStatus
import kotlinx.coroutines.launch

@Composable
fun ArtisanHomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    requestViewModel: RequestViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
) {
    val requestState by requestViewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        requestViewModel.fetchRequests()
        authViewModel.eventFlow.collect { event ->
            if (event is AuthViewModel.AuthEvent.Logout) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.artisan_dashboard),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.ArtisanMessages.route) }) {
                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = stringResource(R.string.messages))
                    }
                    IconButton(onClick = { navController.navigate(Screen.ArtisanProfile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile))
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        },
        bottomBar = {
            ArtisanBottomBar(navController = navController, currentRoute = Screen.ArtisanHome.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            MainButton(
                text = stringResource(R.string.edit_professional_profile),
                onClick = { navController.navigate(Screen.EditArtisanProfile.route) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.service_requests),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = requestState) {
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
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 24.dp),
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
                                        requestViewModel.updateStatus(request.id, status)
                                        
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
                            message = stringResource(R.string.error_message, state.message)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
