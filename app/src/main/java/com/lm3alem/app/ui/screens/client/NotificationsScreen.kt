package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.R
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.viewmodel.RequestViewModel
import com.lm3alem.app.viewmodel.RequestWithUser
import com.lm3alem.app.viewmodel.AuthViewModel
import com.lm3alem.app.data.model.UserRole
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    navController: NavHostController,
    viewModel: RequestViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val authState by authViewModel.authState

    val userRole = (authState as? AuthViewModel.AuthState.Success)?.user?.userRole

    LaunchedEffect(userRole) {
        if (userRole == UserRole.ARTISAN) {
            viewModel.fetchRequests()
        } else if (userRole == UserRole.CLIENT) {
            viewModel.fetchClientRequests()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.notifications),
                onBackClick = { navController.popBackStack() },
                useBrandedColors = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is RequestViewModel.RequestUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LogoBlue)
                    }
                }
                is RequestViewModel.RequestUiState.ClientRequestsLoaded -> {
                    val updates = state.requests
                        .sortedByDescending { it.request.getFormattedDate() }
                    
                    LaunchedEffect(updates) {
                        updates.forEach { 
                            if ((!it.request.readByClient) && (it.request.status != RequestStatus.PENDING)) {
                                viewModel.markAsReadByClient(it.request.id)
                            }
                        }
                    }

                    if (updates.isEmpty()) {
                        EmptyNotifications()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(updates) { item ->
                                NotificationItem(
                                    item = item,
                                    userRole = UserRole.CLIENT,
                                    onReviewClick = {
                                        navController.navigate(Screen.AddReview.createRoute(item.request.artisanId, item.request.id))
                                    }
                                )
                            }
                        }
                    }
                }
                is RequestViewModel.RequestUiState.ArtisanRequestsLoaded -> {
                    val updates = state.requests
                        .sortedByDescending { it.request.getFormattedDate() }
                    
                    LaunchedEffect(updates) {
                        updates.forEach { 
                            if (!it.request.readByArtisan) {
                                viewModel.markAsReadByArtisan(it.request.id)
                            }
                        }
                    }

                    if (updates.isEmpty()) {
                        EmptyNotifications()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(updates) { item ->
                                NotificationItem(
                                    item = item,
                                    userRole = UserRole.ARTISAN,
                                    onReviewClick = {} // Artisan doesn't review clients here usually
                                )
                            }
                        }
                    }
                }
                is RequestViewModel.RequestUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun NotificationItem(
    item: RequestWithUser,
    userRole: UserRole,
    onReviewClick: () -> Unit
) {
    val request = item.request
    val user = item.user
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(request.getFormattedDate())

    val (icon, color, titleRes) = when (request.status) {
        RequestStatus.ACCEPTED -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50), R.string.notification_request_update_title)
        RequestStatus.REFUSED -> Triple(Icons.Default.Cancel, Color(0xFFF44336), R.string.notification_request_update_title)
        RequestStatus.DONE -> Triple(Icons.Default.Info, LogoBlue, R.string.booking_history)
        RequestStatus.PENDING -> Triple(Icons.AutoMirrored.Filled.Send, LogoBlue, if (userRole == UserRole.ARTISAN) R.string.new_request else R.string.request_sent)
    }

    val message = if (userRole == UserRole.ARTISAN) {
        when (request.status) {
            RequestStatus.PENDING -> stringResource(R.string.notification_new_request, user?.fullName ?: stringResource(R.string.client), request.serviceName)
            RequestStatus.ACCEPTED -> stringResource(R.string.notification_artisan_accepted, request.serviceName)
            RequestStatus.REFUSED -> stringResource(R.string.notification_artisan_refused, request.serviceName)
            RequestStatus.DONE -> stringResource(R.string.status_done) + ": " + request.serviceName
        }
    } else {
        when (request.status) {
            RequestStatus.ACCEPTED -> stringResource(R.string.notification_request_accepted, request.serviceName)
            RequestStatus.REFUSED -> stringResource(R.string.notification_request_refused, request.serviceName)
            RequestStatus.DONE -> stringResource(R.string.status_done) + ": " + request.serviceName
            RequestStatus.PENDING -> stringResource(
                R.string.notification_request_sent, 
                request.serviceName, 
                user?.fullName ?: stringResource(R.string.artisan)
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = color.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = color
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (userRole == UserRole.CLIENT && (request.status == RequestStatus.ACCEPTED || request.status == RequestStatus.DONE)) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LogoBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.add_review))
                }
            }
        }
    }
}

@Composable
fun EmptyNotifications() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_requests_yet), // Or a more specific string
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
