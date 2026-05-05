package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.ArtisanCard
import com.lm3alem.app.ui.components.ErrorMessage
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
        Text(
            text = stringResource(R.string.find_artisan),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.filterArtisans(searchQuery, cityFilter)
            },
            label = stringResource(R.string.search_by_job),
            leadingIcon = Icons.Default.Search
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(
            value = cityFilter,
            onValueChange = {
                cityFilter = it
                viewModel.filterArtisans(searchQuery, cityFilter)
            },
            label = stringResource(R.string.filter_by_city)
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState) {
            is ClientViewModel.ClientUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ClientViewModel.ClientUiState.Success -> {
                if (state.artisans.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.no_artisans_found),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.artisans) { artisan ->
                            ArtisanCard(artisan = artisan) {
                                navController.navigate(Screen.ArtisanDetails.createRoute(artisan.userId))
                            }
                        }
                    }
                }
            }
            is ClientViewModel.ClientUiState.Error -> {
                ErrorMessage(message = stringResource(R.string.error_message, state.message))
            }
        }
    }
}
