package com.lm3alem.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.lm3alem.app.R
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val authState by viewModel.authState
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToHome -> {
                    val route =
                        if (event.role == UserRole.CLIENT)
                            Screen.ClientHome.route
                        else
                            Screen.ArtisanHome.route

                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }

                is AuthViewModel.AuthEvent.NavigateToEmailVerification -> {
                    navController.navigate(Screen.VerifyEmail.route)
                }

                is AuthViewModel.AuthEvent.NavigateToRoleSelection -> {
                    navController.navigate(Screen.RoleSelection.route)
                }

                is AuthViewModel.AuthEvent.NavigateToCompleteProfile -> {
                    navController.navigate(Screen.CompleteProfile.route)
                }
                else -> {}
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onBackground = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Circular Logo
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.secondary),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = stringResource(R.string.app_logo),
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lm3alem Text
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = primaryColor)) {
                    append("Lm")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("3")
                }
                withStyle(style = SpanStyle(color = primaryColor)) {
                    append("alem")
                }
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.welcome_back),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password),
            visualTransformation = PasswordVisualTransformation()
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                Text(text = stringResource(R.string.forgot_password), color = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MainButton(
            text = stringResource(R.string.login),
            onClick = { viewModel.login(email, password) },
            isLoading = authState is AuthViewModel.AuthState.Loading,
            containerColor = primaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    try {
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(
                                context.getString(R.string.default_web_client_id)
                            )
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )

                        val credential = result.credential

                        if (
                            credential is CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            viewModel.loginWithGoogle(credential)
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.invalid_google_credential),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            e.message ?: context.getString(R.string.google_sign_in_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = authState !is AuthViewModel.AuthState.Loading
        ) {
            Text(stringResource(R.string.continue_google))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.no_account),
                fontSize = 14.sp,
                color = onBackground
            )
            Text(
                text = stringResource(R.string.register),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.clickable { navController.navigate(Screen.Register.route) }
            )
        }

        if (authState is AuthViewModel.AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = (authState as AuthViewModel.AuthState.Error).message)
        }
    }
}
