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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 24.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = profile.job,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300))
                                Text(
                                    text = " " + stringResource(R.string.rating_reviews_count, String.format(Locale.US, "%.1f", profile.rating), profile.reviewCount),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = profile.city,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.years_experience, profile.experience),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = profile.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MainButton(
                                    text = stringResource(R.string.hire_me),
                                    onClick = {
                                        artisanId?.let {
                                            navController.navigate(Screen.SendRequest.createRoute(it))
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(
                                    onClick = {
                                        artisanId?.let {
                                            navController.navigate(Screen.AddReview.createRoute(it))
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(stringResource(R.string.add_review))
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = stringResource(R.string.reviews),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        when (val rState = reviewState) {
                            is ReviewViewModel.ReviewUiState.Loading -> {
                                item { CircularProgressIndicator() }
                            }
                            is ReviewViewModel.ReviewUiState.ReviewsLoaded -> {
                                if (rState.reviews.isEmpty()) {
                                    item { 
                                        Text(
                                            text = stringResource(R.string.no_reviews_yet),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    }
                                } else {
                                    items(rState.reviews) { review ->
                                        ReviewItem(review)
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
