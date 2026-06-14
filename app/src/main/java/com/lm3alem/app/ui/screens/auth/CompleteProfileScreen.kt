package com.lm3alem.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.AuthViewModel

@Composable
fun CompleteProfileScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val authState by viewModel.authState

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            val user = (authState as AuthViewModel.AuthState.Success).user
            if (fullName.isEmpty()) fullName = user.fullName
            if (phone.isEmpty()) phone = user.phone
            if (city.isEmpty()) city = user.city
            if (imageUrl.isEmpty()) imageUrl = user.imageUrl
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToHome -> {
                    val route = if (event.role == UserRole.CLIENT) {
                        Screen.ClientHome.route
                    } else {
                        Screen.EditArtisanProfile.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.complete_profile),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = LogoBlue
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image Section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, LogoYellow, CircleShape)
                    .clickable { /* Handle image pick */ },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = LogoBlue.copy(alpha = 0.5f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                    AppTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = stringResource(R.string.full_name),
                        leadingIcon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = stringResource(R.string.phone_number),
                        leadingIcon = Icons.Default.Phone
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = stringResource(R.string.city),
                        leadingIcon = Icons.Default.LocationOn
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = stringResource(R.string.profile_image_url),
                        leadingIcon = Icons.Default.Image
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    MainButton(
                        text = stringResource(R.string.save_profile),
                        onClick = {
                            if (fullName.isNotBlank() && phone.isNotBlank() && city.isNotBlank()) {
                                viewModel.completeProfile(fullName, phone, city, imageUrl)
                            }
                        },
                        isLoading = authState is AuthViewModel.AuthState.Loading,
                        enabled = fullName.isNotBlank() && phone.isNotBlank() && city.isNotBlank(),
                        containerColor = LogoBlue
                    )

                    if (authState is AuthViewModel.AuthState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ErrorMessage(message = (authState as AuthViewModel.AuthState.Error).message)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
