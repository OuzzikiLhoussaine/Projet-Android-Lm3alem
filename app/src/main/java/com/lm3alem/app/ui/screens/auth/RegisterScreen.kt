package com.lm3alem.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) }
    
    val authState by viewModel.authState

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AuthViewModel.AuthEvent.NavigateToEmailVerification -> {
                    navController.navigate(Screen.VerifyEmail.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    val navyBlue = Color(0xFF001D3D)
    val goldYellow = Color(0xFFFFC107)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.register),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = navyBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.i_am_a),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = navyBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Role Selection Toggles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoleToggleButton(
                text = stringResource(R.string.client),
                icon = Icons.Default.Person,
                isSelected = selectedRole == UserRole.CLIENT,
                onClick = { selectedRole = UserRole.CLIENT },
                modifier = Modifier.weight(1f),
                selectedColor = navyBlue
            )
            RoleToggleButton(
                text = stringResource(R.string.artisan),
                icon = Icons.Default.Build,
                isSelected = selectedRole == UserRole.ARTISAN,
                onClick = { selectedRole = UserRole.ARTISAN },
                modifier = Modifier.weight(1f),
                selectedColor = navyBlue
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(R.string.confirm_password),
            visualTransformation = PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        val isFormValid = email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword

        MainButton(
            text = stringResource(R.string.register),
            onClick = {
                viewModel.register(email, password, selectedRole)
            },
            enabled = isFormValid,
            isLoading = authState is AuthViewModel.AuthState.Loading,
            containerColor = navyBlue
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.already_have_account),
                fontSize = 14.sp,
                color = navyBlue
            )
            Text(
                text = stringResource(R.string.login),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = navyBlue,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }

        if (authState is AuthViewModel.AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = (authState as AuthViewModel.AuthState.Error).message)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RoleToggleButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) selectedColor else Color.Transparent,
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
