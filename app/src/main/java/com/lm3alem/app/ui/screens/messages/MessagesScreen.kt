package com.lm3alem.app.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.lm3alem.app.R
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow

data class Conversation(
    val id: String,
    val senderName: String,
    val senderProfession: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val imageUrl: String
)

@Composable
fun MessagesScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    
    val mockConversations = listOf(
        Conversation("1", "Ahmed Hassan", "Plumber", "I'll be there at 2 PM tomorrow", "2m ago", 2, true, ""),
        Conversation("2", "Mohamed Ali", "Electrician", "Thanks for choosing my service!", "1h ago", 0, true, ""),
        Conversation("3", "Youssef Ibrahim", "Carpenter", "The project is completed", "3h ago", 1, false, ""),
        Conversation("4", "Khaled Mahmoud", "Painter", "Can we reschedule to next week?", "1d ago", 0, false, ""),
        Conversation("5", "Omar Saeed", "Builder", "I've sent you the quote", "2d ago", 0, true, ""),
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.messages),
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = LogoBlue,
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.ClientHome.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Explore.route) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = stringResource(R.string.explore)) },
                    label = { Text(stringResource(R.string.explore)) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = stringResource(R.string.messages)) },
                    label = { Text(stringResource(R.string.messages)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LogoBlue,
                        selectedTextColor = LogoBlue,
                        indicatorColor = LogoBlue.copy(alpha = 0.1f)
                    )
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_messages), color = Color.LightGray) },
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
                    ConversationItem(conversation) {
                        // Navigate to detail
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Online Status
            Box {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color.LightGray.copy(alpha = 0.2f)
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
                
                // Status dot
                Surface(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = if (conversation.isOnline) Color(0xFF4CAF50) else Color.LightGray,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {}
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.senderName,
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
                
                Text(
                    text = conversation.senderProfession,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (conversation.unreadCount > 0) LogoBlue else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (conversation.unreadCount > 0) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            shape = CircleShape,
                            color = LogoYellow
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = conversation.unreadCount.toString(),
                                    color = LogoBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
