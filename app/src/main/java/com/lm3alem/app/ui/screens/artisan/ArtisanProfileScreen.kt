package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ArtisanBottomBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.components.ProfileMenuItem
import com.lm3alem.app.ui.components.ProfileMenuToggleItem
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.AuthViewModel
import com.lm3alem.app.viewmodel.ArtisanViewModel

@Composable
fun ArtisanProfileScreen(
    navController: NavHostController,
    viewModel: ArtisanViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val artisanWithUser by viewModel.artisanWithUser
    val uiState by viewModel.uiState
    var notificationsEnabled by remember { mutableStateOf(value = true) }

    LaunchedEffect(key1 = true) {
        authViewModel.eventFlow.collect { event ->
            if (event is AuthViewModel.AuthEvent.Logout) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.ArtisanProfile.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.profile),
                useBrandedColors = true
            )
        },
        bottomBar = {
            ArtisanBottomBar(
                navController = navController,
                currentRoute = Screen.ArtisanProfile.route
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                if (artisanWithUser != null) {
                    val user = artisanWithUser?.user
                    val artisan = artisanWithUser?.artisan

                    // Header with Profile Picture
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(LogoBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                if (user?.imageUrl?.isNotEmpty() == true) {
                                    AsyncImage(
                                        model = user.imageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.padding(20.dp),
                                        tint = LogoBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            if (user != null) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (artisan != null) {
                                Text(
                                    text = artisan.job,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = LogoYellow
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Items
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.account_settings),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = stringResource(R.string.edit_profile),
                            subtitle = stringResource(R.string.edit_profile_subtitle),
                            onClick = { navController.navigate(Screen.EditProfile.route) }
                        )
                        
                        ProfileMenuItem(
                            icon = Icons.Default.Work,
                            title = stringResource(R.string.my_projects),
                            subtitle = stringResource(R.string.my_projects_subtitle),
                            onClick = { /* Navigate to projects */ }
                        )
                        
                        ProfileMenuItem(
                            icon = Icons.Default.Star,
                            title = stringResource(R.string.my_reviews),
                            subtitle = stringResource(R.string.my_reviews_subtitle),
                            onClick = { /* Navigate to reviews */ }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = stringResource(R.string.app_settings),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ProfileMenuToggleItem(
                            icon = Icons.Default.Notifications,
                            title = stringResource(R.string.notifications),
                            isChecked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Help,
                            title = stringResource(R.string.help_support),
                            onClick = { /* Navigate to help */ }
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        
                        MainButton(
                            text = stringResource(R.string.logout),
                            onClick = { authViewModel.logout() },
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (uiState is ArtisanViewModel.ArtisanUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = LogoBlue
                )
            }

            if (uiState is ArtisanViewModel.ArtisanUiState.Error) {
                ErrorMessage(
                    message = (uiState as ArtisanViewModel.ArtisanUiState.Error).message,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}
