package com.lm3alem.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.ui.navigation.Screen

@Composable
fun ChooseRoleScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "I am a...", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate(Screen.Register.createRoute(UserRole.CLIENT.name)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Client")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(Screen.Register.createRoute(UserRole.ARTISAN.name)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Artisan / Worker")
        }
    }
}
