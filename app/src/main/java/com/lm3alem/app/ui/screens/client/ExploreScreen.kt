package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.lm3alem.app.R
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.data.model.ArtisanWithUser
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.User
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ClientBottomBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.Lm3alemTheme
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.ClientViewModel
import com.lm3alem.app.viewmodel.RequestViewModel
import com.lm3alem.app.viewmodel.RequestViewModel.RequestUiState
import java.util.Locale

@Composable
fun ExploreScreen(
    navController: NavHostController,
    viewModel: ClientViewModel = hiltViewModel(),
    initialCategory: String? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState
    var selectedCategory by remember { mutableStateOf(initialCategory ?: "All") }
    val categories = listOf("All", "Plumber", "Electrician", "Carpenter", "Painter", "Builder")
    
    val requestViewModel: RequestViewModel = hiltViewModel()
    val requestState by requestViewModel.uiState

    val unreadCount = if (requestState is RequestUiState.ClientRequestsLoaded) {
        (requestState as RequestUiState.ClientRequestsLoaded).requests.count { it.request.readByClient.not() && it.request.status != RequestStatus.PENDING }
    } else 0

    LaunchedEffect(initialCategory) {
        requestViewModel.fetchClientRequests()
        viewModel.filterArtisans("", if (initialCategory == null || initialCategory == "All") "" else initialCategory)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Explore",
                onNotificationClick = { navController.navigate(Screen.Notifications.route) },
                notificationCount = unreadCount
            )
        },
        bottomBar = {
            ClientBottomBar(navController = navController, currentRoute = Screen.Explore.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search and Filter Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.filterArtisans(searchQuery, if (selectedCategory == "All") "" else selectedCategory)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search artisans, services...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LogoBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
                
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { /* Open filters */ },
                    shape = RoundedCornerShape(16.dp),
                    color = LogoBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = LogoYellow
                        )
                    }
                }
            }

            // Categories Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(categories) { category ->
                    CategoryTab(
                        title = category,
                        isSelected = selectedCategory == category,
                        onClick = { 
                            selectedCategory = category
                            viewModel.filterArtisans(searchQuery, if (category == "All") "" else category)
                        }
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

            // Results Summary
            when (val state = uiState) {
                is ClientViewModel.ClientUiState.Success -> {
                    Text(
                        text = "${state.artisans.size} artisans found",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.artisans) { artisanWithUser ->
                            ExploreArtisanCard(artisanWithUser) {
                                navController.navigate(Screen.ArtisanDetails.createRoute(artisanWithUser.artisan.userId))
                            }
                        }
                    }
                }
                is ClientViewModel.ClientUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LogoBlue)
                    }
                }
                is ClientViewModel.ClientUiState.Error -> {
                    ErrorMessage(message = state.message, modifier = Modifier.padding(24.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) LogoBlue else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(modifier = Modifier.width(40.dp).height(3.dp).background(LogoYellow))
        }
    }
}

@Composable
fun ExploreArtisanCard(
    artisanWithUser: ArtisanWithUser,
    onClick: () -> Unit
) {
    val artisan = artisanWithUser.artisan
    val user = artisanWithUser.user

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            ) {
                if (user.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = user.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp), tint = LogoBlue)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName.ifEmpty { "Artisan Name" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LogoBlue
                )
                
                Text(
                    text = artisan.job,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = artisan.city.ifEmpty { "Cairo, Egypt" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < artisan.rating.toInt()) LogoYellow else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.1f", artisan.rating)} (${artisan.reviewCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${artisan.getPriceDouble()} DH/hr",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LogoBlue
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreArtisanCardPreview() {
    Lm3alemTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ExploreArtisanCard(
                artisanWithUser = ArtisanWithUser(
                    artisan = ArtisanProfile(
                        userId = "1",
                        job = "Plumber",
                        rating = 4.8,
                        reviewCount = 127,
                        price = 45.0,
                        isAvailable = true
                    ),
                    user = User(
                        id = "1",
                        fullName = "Ahmed Mansouri",
                        imageUrl = "https://images.unsplash.com/photo-1585704032915-c3400ca1f9e3?auto=format&fit=crop&q=80&w=400"
                    )
                ),
                onClick = {}
            )
            
            ExploreArtisanCard(
                artisanWithUser = ArtisanWithUser(
                    artisan = ArtisanProfile(
                        userId = "2",
                        job = "Electrician",
                        rating = 4.9,
                        reviewCount = 85,
                        price = 60.0,
                        isAvailable = true
                    ),
                    user = User(
                        id = "2",
                        fullName = "Ouzziki Lhoussaine",
                        imageUrl = "https://plus.unsplash.com/premium_photo-1661644847590-3783e3d20652?q=80&w=400&auto=format&fit=crop"
                    )
                ),
                onClick = {}
            )

            ExploreArtisanCard(
                artisanWithUser = ArtisanWithUser(
                    artisan = ArtisanProfile(
                        userId = "3",
                        job = "Electrician",
                        rating = 4.7,
                        reviewCount = 42,
                        price = 55.0,
                        isAvailable = true
                    ),
                    user = User(
                        id = "3",
                        fullName = "Sarah Alami",
                        imageUrl = "https://images.unsplash.com/photo-1581092921461-7d15cc8905de?auto=format&fit=crop&q=80&w=400"
                    )
                ),
                onClick = {}
            )

            ExploreArtisanCard(
                artisanWithUser = ArtisanWithUser(
                    artisan = ArtisanProfile(
                        userId = "4",
                        job = "Painter",
                        rating = 4.6,
                        reviewCount = 28,
                        price = 40.0,
                        isAvailable = true
                    ),
                    user = User(
                        id = "4",
                        fullName = "Hasna Elhassani",
                        imageUrl = "https://plus.unsplash.com/premium_photo-1682974933148-ebe98314f922?q=80&w=400&auto=format&fit=crop"
                    )
                ),
                onClick = {}
            )
        }
    }
}
