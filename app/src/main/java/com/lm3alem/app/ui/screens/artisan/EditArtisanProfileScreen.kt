package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.viewmodel.ArtisanViewModel

@Composable
fun EditArtisanProfileScreen(
    navController: NavHostController,
    viewModel: ArtisanViewModel = hiltViewModel()
) {
    var job by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val uiState by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ArtisanViewModel.ArtisanEvent.ProfileSaved -> {
                    navController.popBackStack()
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextField(
                value = job,
                onValueChange = { job = it },
                label = stringResource(R.string.job_profession)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = stringResource(R.string.description_bio),
                singleLine = false,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = experience,
                onValueChange = { experience = it },
                label = stringResource(R.string.years_of_experience),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = city,
                onValueChange = { city = it },
                label = stringResource(R.string.city)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = price,
                onValueChange = { price = it },
                label = stringResource(R.string.starting_price_dh),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(32.dp))

            MainButton(
                text = stringResource(R.string.save_profile),
                onClick = {
                    if (job.isNotBlank() && city.isNotBlank() && description.isNotBlank()) {
                        viewModel.saveProfile(job, description, experience, city, price)
                    }
                },
                enabled = job.isNotBlank() && city.isNotBlank(),
                isLoading = uiState is ArtisanViewModel.ArtisanUiState.Loading
            )

            if (uiState is ArtisanViewModel.ArtisanUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorMessage(message = stringResource(R.string.error_message, (uiState as ArtisanViewModel.ArtisanUiState.Error).message))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
