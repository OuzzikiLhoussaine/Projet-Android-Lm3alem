package com.lm3alem.app.ui.screens.profile

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
import com.lm3alem.app.viewmodel.AuthViewModel
import com.lm3alem.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    LaunchedEffect(key1 = true) {
        authViewModel.eventFlow.collect { event ->
            if (event is AuthViewModel.AuthEvent.Logout) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.profile),
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile)) },
                    label = { Text(stringResource(R.string.profile)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LogoBlue,
                        selectedTextColor = LogoBlue,
                        indicatorColor = LogoBlue.copy(alpha = 0.1f),
                    ),
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            when (val state = uiState) {
                is ProfileViewModel.ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LogoBlue)
                    }
                }
                is ProfileViewModel.ProfileUiState.Success -> {
                    val user = state.user
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // User Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Surface(
                                    modifier = Modifier.size(80.dp),
                                    shape = CircleShape,
                                    color = Color.LightGray.copy(alpha = 0.2f),
                                ) {
                                    if (user.imageUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = user.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.padding(16.dp),
                                            tint = LogoBlue,
                                        )
                                    }
                                }
                                Surface(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { navController.navigate(Screen.EditProfile.route) },
                                    shape = CircleShape,
                                    color = LogoYellow,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.edit_profile),
                                        modifier = Modifier.padding(6.dp),
                                        tint = LogoBlue,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column {
                                Text(
                                    text = user.fullName.ifEmpty { "User Name" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = LogoBlue
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                if (user.phone.isNotEmpty()) {
                                    Text(
                                        text = user.phone,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Menu Group 1: Activity
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            ProfileMenuItem(
                                icon = Icons.Default.FavoriteBorder,
                                title = "Favorites",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuItem(
                                icon = Icons.Default.History,
                                title = "Booking History",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuItem(
                                icon = Icons.Default.CreditCard,
                                title = "Payment Methods",
                                onClick = { }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Group 2: Settings
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            ProfileMenuToggleItem(
                                icon = Icons.Default.NotificationsNone,
                                title = "Notifications",
                                isChecked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuToggleItem(
                                icon = Icons.Default.DarkMode,
                                title = "Dark Mode",
                                isChecked = darkModeEnabled,
                                onCheckedChange = { darkModeEnabled = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuItem(
                                icon = Icons.Default.Language,
                                title = "Language",
                                subtitle = "English",
                                onClick = { }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuItem(
                                icon = Icons.Default.Settings,
                                title = "Settings",
                                onClick = { }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Group 3: Support
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        ProfileMenuItem(
                            icon = Icons.Default.HelpOutline,
                            title = "Help & Support",
                            onClick = { }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { authViewModel.logout() }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.logout),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD32F2F),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Version 1.0.0",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
                is ProfileViewModel.ProfileUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LogoBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = LogoBlue
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ProfileMenuToggleItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LogoBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = LogoBlue
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LogoYellow,
                checkedTrackColor = LogoYellow.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}
