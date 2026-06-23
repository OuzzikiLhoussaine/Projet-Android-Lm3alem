package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.stringResource
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.*
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.Lm3alemTheme
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.viewmodel.AuthViewModel
import com.lm3alem.app.viewmodel.ClientViewModel

@Composable
fun ClientHomeScreen(
    navController: NavHostController,
    viewModel: ClientViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState
    val authState by authViewModel.authState

    val userName = if (authState is AuthViewModel.AuthState.Success) {
        (authState as AuthViewModel.AuthState.Success).user.fullName.split(" ").firstOrNull() ?: "User"
    } else {
        "User"
    }

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
                onNotificationClick = { /* Handle notifications */ },
            )
        },
        bottomBar = {
            ClientBottomBar(navController = navController, currentRoute = Screen.ClientHome.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.hello_user, userName),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = LogoBlue
                )
                Text(
                    text = stringResource(R.string.client_home_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.filterArtisans(searchQuery, "")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_services), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                viewModel.filterArtisans("", "")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LogoBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
            }

            // Categories Section
            SectionHeader(title = stringResource(R.string.categories)) { 
                navController.navigate(Screen.Explore.route)
            }
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                CategoryCard(
                    title = stringResource(R.string.plumber),
                    icon = Icons.Default.Build,
                ) { navController.navigate(Screen.Explore.createRoute("Plumber")) }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = stringResource(R.string.electrician),
                    icon = Icons.Default.Bolt,
                ) { navController.navigate(Screen.Explore.createRoute("Electrician")) }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = stringResource(R.string.carpenter),
                    icon = Icons.Default.Handyman,
                ) { navController.navigate(Screen.Explore.createRoute("Carpenter")) }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = stringResource(R.string.painter),
                    icon = Icons.Default.FormatPaint,
                ) { navController.navigate(Screen.Explore.createRoute("Painter")) }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = stringResource(R.string.builder),
                    icon = Icons.Default.Engineering,
                ) { navController.navigate(Screen.Explore.createRoute("Builder")) }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = stringResource(R.string.handyman),
                    icon = Icons.Default.Construction,
                ) { navController.navigate(Screen.Explore.createRoute("Handyman")) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Top Rated Artisans Section
            SectionHeader(title = stringResource(R.string.top_rated_artisans)) { 
                navController.navigate(Screen.Explore.route)
            }

            when (val state = uiState) {
                is ClientViewModel.ClientUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LogoBlue)
                    }
                }
                is ClientViewModel.ClientUiState.Success -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.artisans.take(5).forEach { artisanWithUser ->
                            ArtisanCard(artisanWithUser = artisanWithUser) {
                                navController.navigate(Screen.ArtisanDetails.createRoute(artisanWithUser.artisan.userId))
                            }
                        }
                        if (state.artisans.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_artisans_found),
                                modifier = Modifier.padding(vertical = 32.dp).align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is ClientViewModel.ClientUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = LogoBlue
        )
        TextButton(onClick = onSeeAllClick) {
            Text(text = stringResource(R.string.see_all), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientHomeScreenPreview() {
    Lm3alemTheme {
        ClientHomeScreen(navController = rememberNavController())
    }
}
