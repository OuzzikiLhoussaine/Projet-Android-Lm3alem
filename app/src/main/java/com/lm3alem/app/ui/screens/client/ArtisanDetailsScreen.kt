package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
    reviewViewModel: ReviewViewModel = hiltViewModel()
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
            TopAppBar(
                title = { Text("Artisan Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            when (val state = uiState) {
                is ArtisanViewModel.ArtisanUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is ArtisanViewModel.ArtisanUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    artisanProfile?.let { profile ->
                        Text(text = profile.job, style = MaterialTheme.typography.headlineSmall)
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300))
                            Text(text = "${String.format(Locale.US, "%.1f", profile.rating)} (${profile.reviewCount} reviews)", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(text = profile.city, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${profile.experience} years of experience", style = MaterialTheme.typography.bodySmall)
                        Text(text = profile.description, style = MaterialTheme.typography.bodyLarge)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { artisanId?.let { navController.navigate(Screen.SendRequest.createRoute(it)) } },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Hire Me")
                            }
                            OutlinedButton(
                                onClick = { artisanId?.let { navController.navigate(Screen.AddReview.createRoute(it)) } },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add Review")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Reviews", style = MaterialTheme.typography.titleMedium)
                        
                        when (val rState = reviewState) {
                            is ReviewViewModel.ReviewUiState.Loading -> CircularProgressIndicator()
                            is ReviewViewModel.ReviewUiState.ReviewsLoaded -> {
                                if (rState.reviews.isEmpty()) {
                                    Text("No reviews yet.")
                                } else {
                                    LazyColumn {
                                        items(rState.reviews) { review ->
                                            ReviewItem(review)
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: com.lm3alem.app.data.model.Review) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}
