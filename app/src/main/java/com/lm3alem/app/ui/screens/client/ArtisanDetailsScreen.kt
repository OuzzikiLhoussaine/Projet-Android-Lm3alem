package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.ArtisanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanDetailsScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: ArtisanViewModel = hiltViewModel(),
) {
    val artisanProfile by viewModel.artisanProfile
    val uiState by viewModel.uiState

    LaunchedEffect(artisanId) {
        if (artisanId != null) {
            viewModel.fetchArtisanProfile(artisanId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artisan Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            when (val state = uiState) {
                is ArtisanViewModel.ArtisanUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is ArtisanViewModel.ArtisanUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    artisanProfile?.let { profile ->
                        Text(text = profile.job, style = MaterialTheme.typography.headlineSmall)
                        Text(text = profile.city, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${profile.experience} years of experience", style = MaterialTheme.typography.bodySmall)
                        Text(text = profile.description, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { artisanId?.let { navController.navigate(Screen.SendRequest.createRoute(it)) } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Service Request")
                        }
                    }
                }
            }
        }
    }
}
