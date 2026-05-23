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
            NavigationBar(
                containerColor = Color.White,
                contentColor = LogoBlue,
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LogoBlue,
                        selectedTextColor = LogoBlue,
                        indicatorColor = LogoBlue.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Hello, $userName 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = LogoBlue
                )
                Text(
                    text = "Find the perfect professional for your home",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
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
                    placeholder = { Text("Search for services...", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray) },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LogoBlue,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Categories Section
            SectionHeader(title = "Categories") { }
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                CategoryCard(
                    title = "Plumber",
                    icon = Icons.Default.Build,
                ) { viewModel.filterArtisans("Plumber", "") }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = "Electrician",
                    icon = Icons.Default.Bolt,
                ) { viewModel.filterArtisans("Electrician", "") }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = "Carpenter",
                    icon = Icons.Default.Handyman,
                ) { viewModel.filterArtisans("Carpenter", "") }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = "Painter",
                    icon = Icons.Default.FormatPaint,
                ) { viewModel.filterArtisans("Painter", "") }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = "Builder",
                    icon = Icons.Default.Engineering,
                ) { viewModel.filterArtisans("Builder", "") }
                Spacer(modifier = Modifier.height(16.dp))
                CategoryCard(
                    title = "Handyman",
                    icon = Icons.Default.Construction,
                ) { viewModel.filterArtisans("Handyman", "") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Top Rated Artisans Section
            SectionHeader(title = "Top Rated Artisans") { }

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
                                text = "No artisans found",
                                modifier = Modifier.padding(vertical = 32.dp).align(Alignment.CenterHorizontally),
                                color = Color.Gray
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
            Text(text = "See all", color = Color.Gray)
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
