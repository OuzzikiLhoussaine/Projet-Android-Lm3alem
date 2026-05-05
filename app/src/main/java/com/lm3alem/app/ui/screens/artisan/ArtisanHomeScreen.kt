package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lm3alem.app.ui.navigation.Screen

@Composable
fun ArtisanHomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Artisan Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { navController.navigate(Screen.EditArtisanProfile.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Complete Professional Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(Screen.ArtisanRequests.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "View Service Requests")
        }
    }
}
