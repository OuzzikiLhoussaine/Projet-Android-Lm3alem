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
import com.lm3alem.app.data.model.ArtisanWithUser
import com.lm3alem.app.data.model.Review
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.ArtisanViewModel
import com.lm3alem.app.viewmodel.ReviewViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ArtisanDetailsScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: ArtisanViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
) {
    val artisanWithUser by viewModel.artisanWithUser
    val uiState by viewModel.uiState
    val reviewState by reviewViewModel.uiState
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
                title = stringResource(R.string.profile),
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share), tint = Color.White)
                    }
                    IconButton(onClick = { /* Favorite */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = stringResource(R.string.favorite), tint = Color.White)
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
                },
                onMessageClick = {
                    artisanId?.let {
                        navController.navigate(Screen.ChatDetail.createRoute("new", it))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
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
                    artisanWithUser?.let { data ->
                        ArtisanHeaderSection(data)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        SpecializationsSection(data.artisan.specializations)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        ArtisanTabs(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                        
                        // Tab Content
                        Box(modifier = Modifier.padding(24.dp)) {
                            when (selectedTab) {
                                0 -> Text(stringResource(R.string.no_portfolio_yet), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                1 -> {
                                    when (val rState = reviewState) {
                                        is ReviewViewModel.ReviewUiState.Loading -> {
                                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(color = LogoBlue)
                                            }
                                        }
                                        is ReviewViewModel.ReviewUiState.ReviewsLoaded -> {
                                            if (rState.reviews.isEmpty()) {
                                                Text(stringResource(R.string.no_reviews_yet), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            } else {
                                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    rState.reviews.forEach { review ->
                                                        ReviewItem(review)
                                                    }
                                                }
                                            }
                                        }
                                        is ReviewViewModel.ReviewUiState.Error -> {
                                            ErrorMessage(message = rState.message)
                                        }
                                        else -> {}
                                    }
                                }
                                2 -> Text(data.artisan.description.ifEmpty { stringResource(R.string.no_bio) }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtisanHeaderSection(data: ArtisanWithUser) {
    val profile = data.artisan
    val user = data.user
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(100.dp),
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
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(20.dp), tint = LogoBlue)
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = LogoBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.verified),
                            tint = LogoYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.professional_job, profile.job),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < profile.rating.toInt()) LogoYellow else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", profile.rating)} (${profile.reviewCount} ${stringResource(R.string.reviews)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                StatItem(label = stringResource(R.string.projects), value = profile.projectCount.toString())
                StatItem(label = stringResource(R.string.years_exp_label), value = "${profile.getExperienceInt()}+")
                StatItem(label = stringResource(R.string.success_rate), value = "${profile.successRate}%")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Info Rows
            InfoItem(icon = Icons.Default.LocationOn, text = profile.city.ifEmpty { stringResource(R.string.location_not_specified) })
            Spacer(modifier = Modifier.height(12.dp))
            InfoItem(icon = Icons.Default.WorkHistory, text = stringResource(R.string.available_label, profile.availability))
            Spacer(modifier = Modifier.height(12.dp))
            InfoItem(icon = Icons.Default.AttachMoney, text = stringResource(R.string.dh_hour, profile.getPriceDouble().toString()), isPrice = true)
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
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LogoBlue)
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String, isPrice: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = if (isPrice) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
            color = if (isPrice) LogoBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SpecializationsSection(specs: List<String>) {
    if (specs.isEmpty()) return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(R.string.specializations),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LogoBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Displaying tags in a simple layout
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                specs.chunked(2).forEach { rowSpecs ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSpecs.forEach { spec ->
                            TagItem(spec)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagItem(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun ArtisanTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        stringResource(R.string.portfolio),
        stringResource(R.string.reviews),
        stringResource(R.string.about)
    )
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
                        color = if (selectedTab == index) LogoBlue else MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selectedTab == index) {
                        Box(modifier = Modifier.width(40.dp).height(2.dp).background(LogoYellow))
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    }
}

@Composable
fun BottomActionBar(onBookClick: () -> Unit, onMessageClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
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
                Icon(Icons.Default.Call, contentDescription = stringResource(R.string.call), tint = LogoBlue)
            }
            
            OutlinedButton(
                onClick = onMessageClick,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = stringResource(R.string.message), tint = LogoBlue)
            }
            
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogoYellow, contentColor = LogoBlue)
            ) {
                Text(stringResource(R.string.book_now), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < review.rating.toInt()) LogoYellow else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(review.getFormattedDate()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
