package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendRequestScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: RequestViewModel = hiltViewModel()
) {
    var description by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is RequestViewModel.RequestEvent.RequestSent -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.send_request),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.describe_service_needed),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = stringResource(R.string.description),
                singleLine = false,
                minLines = 5
            )
            Spacer(modifier = Modifier.height(32.dp))
            MainButton(
                text = stringResource(R.string.send_request),
                onClick = { artisanId?.let { viewModel.sendRequest(it, description) } },
                isLoading = uiState is RequestViewModel.RequestUiState.Loading,
                enabled = description.isNotBlank()
            )
            if (uiState is RequestViewModel.RequestUiState.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorMessage(message = stringResource(R.string.error_message, (uiState as RequestViewModel.RequestUiState.Error).message))
            }
        }
    }
}
