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
import androidx.compose.material.icons.automirrored.filled.*
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
import com.lm3alem.app.data.model.User
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.Lm3alemTheme
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.ClientViewModel
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

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.filterArtisans("", if (initialCategory == "All") "" else initialCategory)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Explore",
                onNotificationClick = { /* Handle notifications */ }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = LogoBlue
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.ClientHome.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Explore, contentDescription = stringResource(R.string.explore)) },
                    label = { Text(stringResource(R.string.explore)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LogoBlue,
                        selectedTextColor = LogoBlue,
                        indicatorColor = LogoBlue.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Messages.route) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = stringResource(R.string.messages)) },
                    label = { Text(stringResource(R.string.messages)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile)) },
                    label = { Text(stringResource(R.string.profile)) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
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
                        viewModel.filterArtisans(searchQuery, "")
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search artisans, services...", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LogoBlue,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
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
                            viewModel.filterArtisans("", if (category == "All") "" else category)
                        }
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.2f))

            // Results Summary
            when (val state = uiState) {
                is ClientViewModel.ClientUiState.Success -> {
                    Text(
                        text = "${state.artisans.size} artisans found",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
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
            color = if (isSelected) LogoBlue else Color.LightGray
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                color = Color.LightGray.copy(alpha = 0.2f)
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
                    color = Color.Gray
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = artisan.city.ifEmpty { "Cairo, Egypt" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < artisan.rating.toInt()) LogoYellow else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.1f", artisan.rating)} (${artisan.reviewCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${artisan.getPriceDouble()}/hr",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LogoBlue
                    )
                    
                    Surface(
                        color = if (artisan.isAvailable) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (artisan.isAvailable) "Available" else "Busy",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (artisan.isAvailable) Color(0xFF4CAF50) else Color.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreArtisanCardPreview() {
    Lm3alemTheme {
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
                    fullName = "Ahmed Hassan",
                    imageUrl = ""
                )
            ),
            onClick = {}
        )
    }
}
