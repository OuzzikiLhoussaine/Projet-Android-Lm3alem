package com.lm3alem.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    
    val authState by viewModel.authState

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToRoleSelection -> {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AppTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = stringResource(R.string.full_name)
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email)
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
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        MainButton(
            text = stringResource(R.string.register),
            onClick = { viewModel.register(email, password, fullName, phone, city, imageUrl) },
            isLoading = authState is AuthViewModel.AuthState.Loading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { navController.popBackStack() }) {
            Text(text = stringResource(R.string.already_account_login))
        }

        if (authState is AuthViewModel.AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = (authState as AuthViewModel.AuthState.Error).message)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
