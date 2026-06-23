package com.lm3alem.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lm3alem.app.data.model.User
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.viewmodel.AdminViewModel
import com.lm3alem.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavHostController,
    adminViewModel: AdminViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by adminViewModel.usersState
    val searchQuery by adminViewModel.searchQuery
    val selectedRole by adminViewModel.selectedRole

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { adminViewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name or email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedRole == null,
                        onClick = { adminViewModel.onRoleFilterChange(null) },
                        label = { Text("All") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedRole == UserRole.CLIENT,
                        onClick = { adminViewModel.onRoleFilterChange(UserRole.CLIENT) },
                        label = { Text("Clients") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedRole == UserRole.ARTISAN,
                        onClick = { adminViewModel.onRoleFilterChange(UserRole.ARTISAN) },
                        label = { Text("Artisans") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedRole == UserRole.ADMIN,
                        onClick = { adminViewModel.onRoleFilterChange(UserRole.ADMIN) },
                        label = { Text("Admins") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (state) {
                is AdminViewModel.AdminState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AdminViewModel.AdminState.Success -> {
                    val users = (state as AdminViewModel.AdminState.Success).users
                    if (users.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No users found")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(users) { user ->
                                UserItem(user = user, onDelete = { adminViewModel.deleteUser(user.id) })
                            }
                        }
                    }
                }
                is AdminViewModel.AdminState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = (state as AdminViewModel.AdminState.Error).message, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.fullName.ifEmpty { "No Name" }, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Role: ${user.role}",
                    fontSize = 12.sp,
                    color = when(user.userRole) {
                        UserRole.ADMIN -> Color.Red
                        UserRole.ARTISAN -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
                if (user.phone.isNotEmpty()) Text(text = "Phone: ${user.phone}", fontSize = 12.sp)
                if (user.city.isNotEmpty()) Text(text = "City: ${user.city}", fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
            }
        }
    }
}
