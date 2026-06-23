package com.lm3alem.app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.lm3alem.app.ui.components.AppTextField
import com.lm3alem.app.ui.components.AppTopBar
import com.lm3alem.app.ui.components.ErrorMessage
import com.lm3alem.app.ui.components.MainButton
import com.lm3alem.app.ui.navigation.Screen
import com.lm3alem.app.ui.theme.LogoBlue
import com.lm3alem.app.ui.theme.LogoYellow
import com.lm3alem.app.viewmodel.RequestViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendRequestScreen(
    navController: NavHostController,
    artisanId: String?,
    viewModel: RequestViewModel = hiltViewModel()
) {
    var serviceName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedStartTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedEndTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val h = if (hour == 0 || hour == 12) 12 else hour % 12
        return String.format(Locale.getDefault(), "%02d:%02d %s", h, minute, amPm)
    }

    val uiState by viewModel.uiState

    // Handle address returned from MapPicker
    val returnedAddress = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("selected_address")
        ?.observeAsState()

    LaunchedEffect(returnedAddress?.value) {
        returnedAddress?.value?.let {
            address = it
            // Clear the value so it doesn't trigger again
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_address")
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is RequestViewModel.RequestEvent.RequestSent -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.send_request),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Service Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LogoBlue,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = "Service (e.g. Repair water leak) *",
                placeholder = "What service do you need?"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description (Optional)",
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address *",
                trailingIcon = {
                    IconButton(onClick = { navController.navigate(Screen.MapPicker.route) }) {
                        Icon(Icons.Default.Map, contentDescription = "Pick on map", tint = LogoBlue)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = budget,
                onValueChange = { budget = it },
                label = "Budget (DH) *",
                placeholder = "e.g. 300"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Appointment Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LogoBlue,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // Date Picker Field
            OutlinedTextField(
                value = selectedDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                label = { Text("Date *") },
                readOnly = true,
                enabled = false,
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = LogoBlue) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = LogoBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Time
                OutlinedTextField(
                    value = selectedStartTime?.let { formatTime(it.first, it.second) } ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartTimePicker = true },
                    label = { Text("From *") },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // End Time
                OutlinedTextField(
                    value = selectedEndTime?.let { formatTime(it.first, it.second) } ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndTimePicker = true },
                    label = { Text("To *") },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            MainButton(
                text = "Send Request",
                onClick = {
                    artisanId?.let {
                        val dateStr = selectedDate?.let { dateFormatter.format(Date(it)) } ?: ""
                        val startStr = selectedStartTime?.let { formatTime(it.first, it.second) } ?: ""
                        val endStr = selectedEndTime?.let { formatTime(it.first, it.second) } ?: ""
                        
                        viewModel.sendRequest(
                            artisanId = it,
                            serviceName = serviceName,
                            description = description,
                            address = address,
                            budget = budget,
                            startTime = "$dateStr $startStr",
                            endTime = endStr
                        )
                    }
                },
                isLoading = uiState is RequestViewModel.RequestUiState.Loading,
                enabled = serviceName.isNotBlank() && 
                         address.isNotBlank() && 
                         budget.isNotBlank() && 
                         selectedDate != null && 
                         selectedStartTime != null && 
                         selectedEndTime != null
            )

            if (uiState is RequestViewModel.RequestUiState.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                ErrorMessage(message = (uiState as RequestViewModel.RequestUiState.Error).message)
            }
        }

        // Dialogs for picking date and time
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showStartTimePicker) {
            val timePickerState = rememberTimePickerState()
            TimePickerDialog(
                onDismissRequest = { showStartTimePicker = false },
                onConfirm = {
                    selectedStartTime = Pair(timePickerState.hour, timePickerState.minute)
                    showStartTimePicker = false
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }

        if (showEndTimePicker) {
            val timePickerState = rememberTimePickerState()
            TimePickerDialog(
                onDismissRequest = { showEndTimePicker = false },
                onConfirm = {
                    selectedEndTime = Pair(timePickerState.hour, timePickerState.minute)
                    showEndTimePicker = false
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        },
        text = { content() }
    )
}
