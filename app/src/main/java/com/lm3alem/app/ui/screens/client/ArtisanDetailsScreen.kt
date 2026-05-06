package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.ArtisanViewModel

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import com.lm3alem.app.viewmodel.ReviewViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanDetailsScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: ArtisanViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
) {
    val artisanProfile by viewModel.artisanProfile
    val uiState by viewModel.uiState
    val reviewState by reviewViewModel.uiState

    LaunchedEffect(artisanId) {
        if (artisanId != null) {
            viewModel.fetchArtisanProfile(artisanId)
            reviewViewModel.fetchReviews(artisanId)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.artisan_details),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ArtisanViewModel.ArtisanUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ArtisanViewModel.ArtisanUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    ErrorMessage(message = stringResource(R.string.error_message, state.message))
                }
            }
            else -> {
                artisanProfile?.let { profile ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Job: ${profile.job}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Description: ${profile.description}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Experience: ${profile.getExperienceInt()} ans", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "City: ${profile.city}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Price: ${profile.getPriceDouble()} DH", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Rating: ${String.format(Locale.US, "%.1f", profile.rating)}/5", style = MaterialTheme.typography.bodyLarge)

                        Spacer(modifier = Modifier.height(32.dp))

                        MainButton(
                            text = "Send request",
                            onClick = {
                                artisanId?.let {
                                    navController.navigate(Screen.SendRequest.createRoute(it))
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        MainButton(
                            text = "Add review",
                            onClick = {
                                artisanId?.let {
                                    navController.navigate(Screen.AddReview.createRoute(it))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: com.lm3alem.app.data.model.Review) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < review.rating) Color(0xFFFFB300) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        if (review.comment.isNotEmpty()) {
            Text(text = review.comment, style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
