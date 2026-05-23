package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.ArtisanViewModel
import com.lm3alem.app.viewmodel.ReviewViewModel
import java.util.Locale

@Composable
fun ArtisanDetailsScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: ArtisanViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
) {
    val artisanProfile by viewModel.artisanProfile
    val uiState by viewModel.uiState
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(artisanId) {
        if (artisanId != null) {
            viewModel.fetchArtisanProfile(artisanId)
            reviewViewModel.fetchReviews(artisanId)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Profile",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                    IconButton(onClick = { /* Favorite */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BottomActionBar(
                onBookClick = {
                    artisanId?.let {
                        navController.navigate(Screen.SendRequest.createRoute(it))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            when (val state = uiState) {
                is ArtisanViewModel.ArtisanUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LogoBlue)
                    }
                }
                is ArtisanViewModel.ArtisanUiState.Error -> {
                    ErrorMessage(message = state.message, modifier = Modifier.padding(24.dp))
                }
                else -> {
                    artisanProfile?.let { profile ->
                        ArtisanHeaderSection(profile)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        SpecializationsSection(profile.specializations)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        ArtisanTabs(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                        
                        // Tab Content
                        Box(modifier = Modifier.padding(24.dp)) {
                            when (selectedTab) {
                                0 -> Text("Portfolio content goes here...", color = Color.Gray)
                                1 -> Text("Reviews content goes here...", color = Color.Gray)
                                2 -> Text(profile.description.ifEmpty { "No bio available." }, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtisanHeaderSection(profile: com.lm3alem.app.data.model.ArtisanProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color.LightGray.copy(alpha = 0.2f)
                ) {
                    // Using mock image for now, ideally user imageUrl should be here
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(20.dp), tint = LogoBlue)
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ahmed Hassan", // Mock name, should be user.fullName
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = LogoBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = LogoYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Professional ${profile.job}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < profile.rating.toInt()) LogoYellow else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", profile.rating)} (${profile.reviewCount} reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Projects", value = "127")
                StatItem(label = "Years Exp.", value = "${profile.getExperienceInt()}+")
                StatItem(label = "Success Rate", value = "${profile.successRate}%")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Info Rows
            InfoItem(icon = Icons.Default.LocationOn, text = "Downtown, Cairo") // profile.city
            Spacer(modifier = Modifier.height(12.dp))
            InfoItem(icon = Icons.Default.WorkHistory, text = "Available: ${profile.availability}")
            Spacer(modifier = Modifier.height(12.dp))
            InfoItem(icon = Icons.Default.AttachMoney, text = "${profile.getPriceDouble()} DH/hour", isPrice = true)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Surface(
        modifier = Modifier
            .width(90.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3F7FF)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LogoBlue)
            Text(text = label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String, isPrice: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = if (isPrice) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
            color = if (isPrice) LogoBlue else Color.Gray
        )
    }
}

@Composable
fun SpecializationsSection(specs: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Specializations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LogoBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // FlowRow equivalent using wrap
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val mockSpecs = listOf("Pipe Installation", "Leak Repair", "Water Heaters", "Drain Cleaning", "Fixtures", "Emergency Service")
                val displaySpecs = if (specs.isEmpty()) mockSpecs else specs
                
                // Simplified row-based layout for tags
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagItem(displaySpecs[0])
                    TagItem(displaySpecs[1])
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagItem(displaySpecs[2])
                    TagItem(displaySpecs[3])
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagItem(displaySpecs[4])
                    TagItem(displaySpecs[5])
                }
            }
        }
    }
}

@Composable
fun TagItem(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF3F7FF)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = LogoBlue
        )
    }
}

@Composable
fun ArtisanTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Portfolio", "Reviews", "About")
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEachIndexed { index, title ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onTabSelected(index) }
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == index) LogoBlue else Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selectedTab == index) {
                        Box(modifier = Modifier.width(40.dp).height(2.dp).background(LogoYellow))
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.2f))
    }
}

@Composable
fun BottomActionBar(onBookClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { /* Call */ },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = LogoBlue)
            }
            
            OutlinedButton(
                onClick = { /* Message */ },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Message", tint = LogoBlue)
            }
            
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogoYellow, contentColor = LogoBlue)
            ) {
                Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
