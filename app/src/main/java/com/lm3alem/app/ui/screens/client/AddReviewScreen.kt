package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.lm3alem.app.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ReviewViewModel.ReviewEvent.ReviewAdded -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.add_review),
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
                text = stringResource(R.string.how_was_experience),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                (1..5).forEach { index ->
                    IconButton(
                        onClick = { rating = index.toFloat() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (index <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index <= rating) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppTextField(
                value = comment,
                onValueChange = { comment = it },
                label = stringResource(R.string.comment_optional),
                singleLine = false,
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            MainButton(
                text = stringResource(R.string.submit_review),
                onClick = { artisanId?.let { viewModel.addReview(it, rating, comment) } },
                isLoading = uiState is ReviewViewModel.ReviewUiState.Loading,
                enabled = rating > 0
            )
            
            if (uiState is ReviewViewModel.ReviewUiState.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorMessage(message = stringResource(R.string.error_message, (uiState as ReviewViewModel.ReviewUiState.Error).message))
            }
        }
    }
}
