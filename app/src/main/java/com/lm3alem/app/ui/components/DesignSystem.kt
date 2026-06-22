package com.lm3alem.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.lm3alem.app.R
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.data.model.ArtisanWithUser
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.ServiceRequest
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.Lm3alemTheme
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import java.util.Locale

@Composable
fun ArtisanBottomBar(
    navController: androidx.navigation.NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = LogoBlue
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.ArtisanHome.route,
            onClick = { 
                if (currentRoute != Screen.ArtisanHome.route) {
                    navController.navigate(Screen.ArtisanHome.route) {
                        popUpTo(Screen.ArtisanHome.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = stringResource(R.string.home)) },
            label = { Text(stringResource(R.string.home)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LogoBlue,
                selectedTextColor = LogoBlue,
                indicatorColor = LogoBlue.copy(alpha = 0.1f),
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.ArtisanRequests.route,
            onClick = { 
                if (currentRoute != Screen.ArtisanRequests.route) {
                    navController.navigate(Screen.ArtisanRequests.route)
                }
            },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = stringResource(R.string.requests)) },
            label = { Text(stringResource(R.string.requests)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LogoBlue,
                selectedTextColor = LogoBlue,
                indicatorColor = LogoBlue.copy(alpha = 0.1f),
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.ArtisanMessages.route,
            onClick = { 
                if (currentRoute != Screen.ArtisanMessages.route) {
                    navController.navigate(Screen.ArtisanMessages.route)
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = stringResource(R.string.messages)) },
            label = { Text(stringResource(R.string.messages)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LogoBlue,
                selectedTextColor = LogoBlue,
                indicatorColor = LogoBlue.copy(alpha = 0.1f),
            ),
        )
    }
}

@Composable
fun ClientBottomBar(
    navController: androidx.navigation.NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = LogoBlue
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.ClientHome.route,
            onClick = { 
                if (currentRoute != Screen.ClientHome.route) {
                    navController.navigate(Screen.ClientHome.route) {
                        popUpTo(Screen.ClientHome.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
            label = { Text(stringResource(R.string.home)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LogoBlue,
                selectedTextColor = LogoBlue,
                indicatorColor = LogoBlue.copy(alpha = 0.1f),
            ),
        )
        NavigationBarItem(
            selected = currentRoute?.startsWith("explore") == true,
            onClick = { 
                if (currentRoute?.startsWith("explore") != true) {
                    navController.navigate(Screen.Explore.createRoute())
                }
            },
            icon = { Icon(Icons.Default.Explore, contentDescription = stringResource(R.string.explore)) },
            label = { Text(stringResource(R.string.explore)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LogoBlue,
                selectedTextColor = LogoBlue,
                indicatorColor = LogoBlue.copy(alpha = 0.1f),
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Messages.route,
            onClick = { 
                if (currentRoute != Screen.Messages.route) {
                    navController.navigate(Screen.Messages.route)
                }
            },
            icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = stringResource(R.string.messages)) },
            label = { Text(stringResource(R.string.messages)) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LogoBlue,
                selectedTextColor = LogoBlue,
                indicatorColor = LogoBlue.copy(alpha = 0.1f),
            ),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = { 
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route)
                }
            },
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
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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

@Composable
fun MainButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = contentColor,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String? = null,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it, color = Color.Gray) } },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
        )
        if (isError && (errorMessage != null)) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

@Composable
fun ArtisanCard(artisanWithUser: ArtisanWithUser, onClick: () -> Unit) {
    val artisan = artisanWithUser.artisan
    val user = artisanWithUser.user
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp), // Coins plus arrondis pour plus de confort
        colors = CardDefaults.cardColors(
            containerColor = Color.White, // Fond blanc forcé pour la lisibilité
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Ombre plus douce
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(70.dp),
                shape = CircleShape,
                color = Color(0xFFF0F2F5)
            ) {
                if (user.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = user.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(18.dp),
                        tint = LogoBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName.ifEmpty { stringResource(R.string.full_name) }, 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LogoBlue
                )
                Text(
                    text = artisan.job,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = LogoYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.1f", artisan.rating)} (${artisan.reviewCount})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.price_per_hr, artisan.getPriceDouble().toString()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = LogoBlue
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = LogoBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder for image with overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            ) {
                // Background image (simulated with a dark navy if no URL)
                Box(modifier = Modifier.fillMaxSize().background(LogoBlue.copy(alpha = 0.8f)))
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LogoYellow,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RequestCard(request: ServiceRequest, onStatusUpdate: (RequestStatus) -> Unit) {
    val statusColor = when (request.status) {
        RequestStatus.PENDING -> MaterialTheme.colorScheme.secondary
        RequestStatus.ACCEPTED -> Color(0xFF4CAF50)
        RequestStatus.REFUSED -> MaterialTheme.colorScheme.error
        RequestStatus.DONE -> MaterialTheme.colorScheme.primary
    }
    
    val statusText = when (request.status) {
        RequestStatus.PENDING -> stringResource(R.string.status_pending)
        RequestStatus.ACCEPTED -> stringResource(R.string.status_accepted)
        RequestStatus.REFUSED -> stringResource(R.string.status_refused)
        RequestStatus.DONE -> stringResource(R.string.status_done)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (request.budget.isNotEmpty()) {
                    Text(
                        text = "${request.budget} DH",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LogoBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            if (request.serviceName.isNotEmpty()) {
                Text(
                    text = request.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LogoBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = request.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (request.address.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = request.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            if (request.startTime.isNotEmpty() || request.endTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${request.startTime} - ${request.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            if (request.status == RequestStatus.PENDING || request.status == RequestStatus.ACCEPTED) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (request.status == RequestStatus.PENDING) {
                        TextButton(
                            onClick = { onStatusUpdate(RequestStatus.REFUSED) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        ) {
                            Text(stringResource(R.string.refuse))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onStatusUpdate(RequestStatus.ACCEPTED) },
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(stringResource(R.string.accept))
                        }
                    } else {
                        Button(
                            onClick = { onStatusUpdate(RequestStatus.DONE) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        ) {
                            Text(stringResource(R.string.mark_as_done))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    useBrandedColors: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (useBrandedColors) LogoYellow else MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (useBrandedColors) FontWeight.Bold else FontWeight.Normal
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = LogoYellow)) {
                            append("Lm")
                        }
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append("3")
                        }
                        withStyle(style = SpanStyle(color = LogoYellow)) {
                            append("alem")
                        }
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            onBackClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = if (useBrandedColors) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            if (onNotificationClick != null) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notifications),
                        tint = Color.White
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (useBrandedColors) LogoBlue else MaterialTheme.colorScheme.background,
            titleContentColor = if (useBrandedColors) Color.White else MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = if (useBrandedColors) Color.White else MaterialTheme.colorScheme.onBackground
        ),
    )
}

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ArtisanCardPreview() {
    Lm3alemTheme {
        ArtisanCard(
            artisanWithUser = ArtisanWithUser(
                artisan = ArtisanProfile(
                    userId = "1",
                    job = "Plumber",
                    rating = 4.8,
                    reviewCount = 127,
                    price = 45.0,
                ),
                user = User(
                    id = "1",
                    fullName = "Ahmed Hassan",
                    imageUrl = ""
                )
            ),
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    Lm3alemTheme {
        CategoryCard(
            title = "Electrician",
            icon = Icons.Default.Bolt,
        ) { }
    }
}
