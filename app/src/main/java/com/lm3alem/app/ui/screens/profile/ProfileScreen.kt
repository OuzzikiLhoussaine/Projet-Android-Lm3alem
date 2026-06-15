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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ClientBottomBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.ProfileMenuItem
import com.lm3alem.app.ui.components.ProfileMenuToggleItem
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
    var showLanguageBottomSheet by remember { mutableStateOf(false) }

    val currentLanguage = remember(showLanguageBottomSheet) {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) {
            "English" // Default to English as we made values/strings.xml English
        } else {
            when (locales.get(0)?.language) {
                "en" -> "English"
                "fr" -> "Français"
                "ar" -> "العربية"
                else -> "English"
            }
        }
    }

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

    if (showLanguageBottomSheet) {
        LanguageBottomSheet(
            onDismiss = { showLanguageBottomSheet = false },
            onLanguageSelected = { languageCode ->
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
                showLanguageBottomSheet = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.profile),
            )
        },
        bottomBar = {
            ClientBottomBar(navController = navController, currentRoute = Screen.Profile.route)
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
                                    text = user.fullName.ifEmpty { stringResource(R.string.full_name) },
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
                        ProfileMenuItem(
                            icon = Icons.Default.History,
                            title = stringResource(R.string.booking_history),
                            onClick = { }
                        )
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
                                title = stringResource(R.string.notifications),
                                isChecked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuItem(
                                icon = Icons.Default.Language,
                                title = stringResource(R.string.language),
                                subtitle = currentLanguage,
                                onClick = { showLanguageBottomSheet = true }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                            ProfileMenuItem(
                                icon = Icons.Default.Settings,
                                title = stringResource(R.string.settings),
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
                            title = stringResource(R.string.help_support),
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
                        text = stringResource(R.string.version, "1.0.0"),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.select_language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            LanguageOption(
                label = stringResource(R.string.english),
                onClick = { onLanguageSelected("en") }
            )
            LanguageOption(
                label = stringResource(R.string.french),
                onClick = { onLanguageSelected("fr") }
            )
            LanguageOption(
                label = stringResource(R.string.arabic),
                onClick = { onLanguageSelected("ar") }
            )
        }
    }
}

@Composable
fun LanguageOption(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
