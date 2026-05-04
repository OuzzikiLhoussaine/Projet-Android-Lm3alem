package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.ClientViewModel

@Composable
fun ClientHomeScreen(
    navController: NavHostController,
    viewModel: ClientViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var cityFilter by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Find an Artisan", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.filterArtisans(searchQuery, cityFilter)
            },
            label = { Text("Search by job") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cityFilter,
            onValueChange = {
                cityFilter = it
                viewModel.filterArtisans(searchQuery, cityFilter)
            },
            label = { Text("Filter by city") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is ClientViewModel.ClientUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ClientViewModel.ClientUiState.Success -> {
                if (state.artisans.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No artisans found")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.artisans) { artisan ->
                            ArtisanCard(artisan = artisan) {
                                navController.navigate(Screen.ArtisanDetails.createRoute(artisan.userId))
                            }
                        }
                    }
                }
            }
            is ClientViewModel.ClientUiState.Error -> {
                Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ArtisanCard(artisan: ArtisanProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artisan.job, style = MaterialTheme.typography.titleLarge)
            Text(text = artisan.city, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = artisan.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${artisan.experience} years exp", style = MaterialTheme.typography.labelMedium)
                Text(text = "${artisan.price} DH", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
