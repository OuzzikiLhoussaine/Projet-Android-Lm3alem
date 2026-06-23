package com.lm3alem.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ProfileMenuItem
import com.lm3alem.app.ui.components.ProfileMenuToggleItem
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    var notificationsEnabled by remember { mutableStateOf(value = true) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings),
                onBackClick = { navController.popBackStack() },
                useBrandedColors = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.account_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = stringResource(R.string.edit_profile),
                    onClick = { navController.navigate(Screen.EditProfile.route) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    ProfileMenuToggleItem(
                        icon = Icons.Default.NotificationsNone,
                        title = stringResource(R.string.notifications),
                        isChecked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ProfileMenuItem(
                        icon = Icons.Default.Description,
                        title = stringResource(R.string.terms_of_service),
                        onClick = { navController.navigate(Screen.TermsOfService.route) }
                    )
                }
            }
        }
    }
}
