package com.lm3alem.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel

@Composable
fun RoleSelectionScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToHome -> {
                    val route = if (event.role == UserRole.CLIENT) Screen.ClientHome.route else Screen.ArtisanHome.route
                    navController.navigate(route) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Complete your profile", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "I am a...", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.selectRole(UserRole.CLIENT) },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthViewModel.AuthState.Loading
        ) {
            Text(text = "Client")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.selectRole(UserRole.ARTISAN) },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthViewModel.AuthState.Loading
        ) {
            Text(text = "Artisan / Worker")
        }

        if (authState is AuthViewModel.AuthState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (authState is AuthViewModel.AuthState.Error) {
            Text(
                text = (authState as AuthViewModel.AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
