package com.lm3alem.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.ProfileUiState.Success) {
            val user = (uiState as ProfileViewModel.ProfileUiState.Success).user
            fullName = user.fullName
            phone = user.phone
            city = user.city
            imageUrl = user.imageUrl
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            if (event is ProfileViewModel.ProfileEvent.ProfileUpdated) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.edit_profile),
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
                value = fullName,
                onValueChange = { fullName = it },
                label = stringResource(R.string.full_name)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(R.string.phone_number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = city,
                onValueChange = { city = it },
                label = stringResource(R.string.city)
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = stringResource(R.string.profile_image_url)
            )
            Spacer(modifier = Modifier.height(32.dp))

            MainButton(
                text = stringResource(R.string.save_profile),
                onClick = {
                    viewModel.updateProfile(fullName, phone, city, imageUrl)
                },
                isLoading = uiState is ProfileViewModel.ProfileUiState.Loading,
                enabled = fullName.isNotBlank()
            )
        }
    }
}
