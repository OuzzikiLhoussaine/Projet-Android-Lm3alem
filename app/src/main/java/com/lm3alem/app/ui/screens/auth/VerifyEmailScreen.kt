package com.lm3alem.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import androidx.compose.runtime.LaunchedEffect
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun VerifyEmailScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by viewModel.authState
    val navyBlue = Color(0xFF001D3D)
    val goldYellow = Color(0xFFFFC107)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToLogin -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.VerifyEmail.route) { inclusive = true }
                    }
                }
                is AuthViewModel.AuthEvent.Logout -> {
                     navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Logo
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            border = BorderStroke(3.dp, goldYellow),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lm3alem Text
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = navyBlue)) {
                    append("Lm")
                }
                withStyle(style = SpanStyle(color = goldYellow)) {
                    append("3")
                }
                withStyle(style = SpanStyle(color = navyBlue)) {
                    append("alem")
                }
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Verify your email",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = navyBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've sent a verification link to your email address. Please click the link in the email to continue.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        MainButton(
            text = "I have verified",
            onClick = { viewModel.checkEmailVerificationStatus() },
            isLoading = authState is AuthViewModel.AuthState.Loading,
            containerColor = navyBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.resendVerificationEmail() },
            enabled = authState !is AuthViewModel.AuthState.Loading
        ) {
            Text(text = "Resend verification email", color = navyBlue)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { viewModel.logout() }) {
            Text(text = "Use a different account", color = Color.Gray)
        }

        if (authState is AuthViewModel.AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = (authState as AuthViewModel.AuthState.Error).message)
        }
    }
}
