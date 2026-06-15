package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppDropdownField
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.viewmodel.ArtisanViewModel

@Composable
fun EditArtisanProfileScreen(
    navController: NavHostController,
    viewModel: ArtisanViewModel = hiltViewModel()
) {
    var job by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val uiState by viewModel.uiState
    val artisanWithUser by viewModel.artisanWithUser

    val categories = listOf("Plumber", "Electrician", "Carpenter", "Painter", "Builder", "Handyman")

    LaunchedEffect(artisanWithUser) {
        artisanWithUser?.artisan?.let { artisan ->
            job = artisan.job
            description = artisan.description
            experience = artisan.getExperienceInt().toString()
            price = artisan.getPriceDouble().toString()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ArtisanViewModel.ArtisanEvent.ProfileSaved -> {
                    navController.navigate(Screen.ArtisanHome.route) {
                        popUpTo(Screen.EditArtisanProfile.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.edit_professional_profile),
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.complete_professional_profile),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = LogoBlue,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppDropdownField(
                        value = job,
                        onValueChange = { job = it },
                        label = stringResource(R.string.job_profession),
                        options = categories,
                        leadingIcon = Icons.Default.Work
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = stringResource(R.string.description_bio),
                        singleLine = false,
                        minLines = 3,
                        leadingIcon = Icons.Default.Description
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = stringResource(R.string.years_of_experience),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = Icons.Default.History
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = stringResource(R.string.starting_price_dh),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = Icons.Default.AttachMoney
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    MainButton(
                        text = stringResource(R.string.save_profile),
                        onClick = {
                            if (job.isNotBlank() && description.isNotBlank()) {
                                viewModel.saveProfile(job, description, experience, price)
                            }
                        },
                        enabled = job.isNotBlank() && description.isNotBlank(),
                        isLoading = uiState is ArtisanViewModel.ArtisanUiState.Loading,
                        containerColor = LogoBlue
                    )

                    if (uiState is ArtisanViewModel.ArtisanUiState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ErrorMessage(message = stringResource(R.string.error_message, (uiState as ArtisanViewModel.ArtisanUiState.Error).message))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
