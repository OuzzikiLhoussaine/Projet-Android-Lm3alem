package com.lm3alem.app.ui.screens.profile

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
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image Section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
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
                
                // Overlay "Edit" badge
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

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.change_profile_picture),
                style = MaterialTheme.typography.labelLarge,
                color = LogoBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            viewModel.updateProfile(fullName, phone, city, imageUrl)
                        },
                        isLoading = uiState is ProfileViewModel.ProfileUiState.Loading,
                        enabled = fullName.isNotBlank() && phone.isNotBlank() && city.isNotBlank(),
                        containerColor = LogoBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
