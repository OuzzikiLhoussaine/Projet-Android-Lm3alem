package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen

@Composable
fun ArtisanHomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.artisan_dashboard))
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MainButton(
                text = stringResource(R.string.complete_professional_profile),
                onClick = { navController.navigate(Screen.EditArtisanProfile.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MainButton(
                text = stringResource(R.string.view_service_requests),
                onClick = { navController.navigate(Screen.ArtisanRequests.route) },
                containerColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
