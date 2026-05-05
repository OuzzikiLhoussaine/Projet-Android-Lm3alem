package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.ArtisanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanDetailsScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: ArtisanViewModel = hiltViewModel()
) {
    var artisanProfile by remember { mutableStateOf<ArtisanProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Using a simple fetch logic here or we could extend ArtisanViewModel
    // For simplicity, we'll assume we fetch by ID. 
    // In a real app, I'd add a dedicated function to ArtisanRepository/ViewModel.

    LaunchedEffect(artisanId) {
        if (artisanId != null) {
            // Ideally call viewModel.fetchArtisanById(artisanId)
            // But I'll simulate or suggest adding it.
            isLoading = false
            // artisanProfile = ...
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artisan Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = "Details for Artisan ID: $artisanId", style = MaterialTheme.typography.headlineSmall)
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
