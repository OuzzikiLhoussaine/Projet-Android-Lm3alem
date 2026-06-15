package com.lm3alem.app.ui.screens.artisan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ArtisanBottomBar
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.AuthViewModel

data class ClientConversation(
    val id: String,
    val clientName: String,
    val location: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val imageUrl: String,
)

@Composable
fun ArtisanMessagesScreen(
    navController: NavHostController,
) {
    var searchQuery by remember { mutableStateOf("") }

    val mockConversations = listOf(
        ClientConversation("1", "Sarah Mansour", "Casablanca", "When can you come for the leak?", "10m ago", 1, isOnline = true, ""),
        ClientConversation("2", "Karim Alami", "Rabat", "The price is okay for me. Let's do it.", "2h ago", 0, isOnline = false, ""),
        ClientConversation("3", "Meryem Bennani", "Marrakech", "Thank you for the quick fix!", "Yesterday", 0, isOnline = true, ""),
        ClientConversation("4", "Anas Zaki", "Fes", "Can we reschedule to Friday?", "2 days ago", 0, isOnline = false, ""),
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.messages),
                actions = {
                    IconButton(onClick = { /* Settings or Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            ArtisanBottomBar(navController = navController, currentRoute = Screen.ArtisanMessages.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Search Bar for Clients
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search clients...", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockConversations) { conversation ->
                    ClientConversationItem(conversation) {
                        // Navigate to detail chat
                    }
                }
            }
        }
    }
}

@Composable
fun ClientConversationItem(conversation: ClientConversation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (conversation.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = conversation.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = LogoBlue
                        )
                    }
                }
                
                if (conversation.isOnline) {
                    Surface(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {}
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LogoBlue
                    )
                    Text(
                        text = conversation.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = conversation.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (conversation.unreadCount > 0) LogoBlue else Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (conversation.unreadCount > 0) {
                Surface(
                    modifier = Modifier.size(20.dp).padding(start = 4.dp),
                    shape = CircleShape,
                    color = LogoYellow
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = LogoBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
