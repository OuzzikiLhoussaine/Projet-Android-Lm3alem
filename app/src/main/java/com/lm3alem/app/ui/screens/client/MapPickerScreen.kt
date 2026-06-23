package com.lm3alem.app.ui.screens.client

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.theme.LogoBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun MapPickerScreen(
    navController: NavHostController,
    onLocationSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Initialize Places if not initialized
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            try {
                val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
                if (apiKey != null && apiKey.isNotEmpty() && apiKey != "YOUR_API_KEY_HERE") {
                    Places.initialize(context, apiKey)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val casablanca = LatLng(33.5731, -7.5898)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(casablanca, 12f)
    }

    var selectedAddress by remember { mutableStateOf("Select location...") }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    // Launcher for Google Places Autocomplete
    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            place.latLng?.let { latLng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                selectedAddress = place.address ?: place.name ?: "Selected Location"
            }
        }
    }

    // Update address when map stops moving
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            withContext(Dispatchers.IO) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(center.latitude, center.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                selectedAddress = addresses[0].getAddressLine(0) ?: "Unknown location"
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1)
                        if (addresses?.isNotEmpty() == true) {
                            withContext(Dispatchers.Main) {
                                selectedAddress = addresses[0].getAddressLine(0) ?: "Unknown location"
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Pick Location",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onLocationSelected(selectedAddress)
                    navController.popBackStack()
                },
                containerColor = LogoBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Check, contentDescription = "Select")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            )
            
            // Search Bar on top of map
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        try {
                            if (Places.isInitialized()) {
                                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                                    .setCountry("MA")
                                    .build(context)
                                autocompleteLauncher.launch(intent)
                            } else {
                                // Fallback or alert
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search address...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            // Center marker (static pin)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.Center)
                    .offset(y = (-22).dp),
                tint = Color.Red
            )

            // Address display at the bottom
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 72.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selected Address",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
