package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ArtisanCard
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel
import com.lm3alem.app.viewmodel.ClientViewModel

@Composable
fun ClientHomeScreen(
    navController: NavHostController,
    viewModel: ClientViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var cityFilter by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = true) {
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
                title = stringResource(R.string.find_artisan),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile))
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
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
}
