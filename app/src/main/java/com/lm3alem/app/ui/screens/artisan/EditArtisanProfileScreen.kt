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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.edit_professional_profile), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = job,
            onValueChange = { job = it },
            label = { Text(stringResource(R.string.job_profession)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description_bio)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text(stringResource(R.string.years_of_experience)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text(stringResource(R.string.city)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text(stringResource(R.string.starting_price_dh)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (job.isBlank() || city.isBlank() || description.isBlank()) {
                    // Show a simple error if required fields are missing
                    return@Button
                }
                viewModel.saveProfile(job, description, experience, city, price)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is ArtisanViewModel.ArtisanUiState.Loading && job.isNotBlank() && city.isNotBlank()
        ) {
            if (uiState is ArtisanViewModel.ArtisanUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(stringResource(R.string.save_profile))
            }
        }

        if (uiState is ArtisanViewModel.ArtisanUiState.Error) {
            Text(
                text = stringResource(R.string.error_message, (uiState as ArtisanViewModel.ArtisanUiState.Error).message),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
