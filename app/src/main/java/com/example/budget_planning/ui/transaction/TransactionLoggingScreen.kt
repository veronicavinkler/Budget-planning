package com.example.budget_planning.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.ui.categories.parseColor
import com.example.budget_planning.ui.dashboard.getIconForName
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = { Text(text = title) },
        text = { content() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionLoggingScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date)

    LaunchedEffect(uiState.isTransactionSaved, uiState.isTransactionDeleted) {
        if (uiState.isTransactionSaved || uiState.isTransactionDeleted) {
            viewModel.resetSaveState()
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.transactionId == null) "Add Transaction" else "Edit Transaction", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.transactionId != null) {
                        IconButton(onClick = viewModel::deleteTransaction) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Transaction Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeButton(
                    text = "Expense",
                    isSelected = !uiState.isIncome,
                    onClick = { viewModel.onTypeChange(false) },
                    modifier = Modifier.weight(1f),
                    selectedColor = Color(0xFFF44336)
                )
                TypeButton(
                    text = "Income",
                    isSelected = uiState.isIncome,
                    onClick = { viewModel.onTypeChange(true) },
                    modifier = Modifier.weight(1f),
                    selectedColor = Color(0xFF4CAF50)
                )
            }

            // Amount Input
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("€ ") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Date Picker Field
            val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dateFormatter.format(Date(uiState.date)),
                    onValueChange = { },
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                var showTimePicker by remember { mutableStateOf(false) }
                val timePickerState = rememberTimePickerState(
                    initialHour = uiState.time.split(":").getOrNull(0)?.toIntOrNull() ?: 12,
                    initialMinute = uiState.time.split(":").getOrNull(1)?.toIntOrNull() ?: 0
                )

                OutlinedTextField(
                    value = uiState.time,
                    onValueChange = { },
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val formattedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                                viewModel.onTimeChange(formattedTime)
                                showTimePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }
            }

            // Category Selection (Dropdown)
            val filteredCategories = uiState.categories.filter { it.isIncome == uiState.isIncome }
            var expanded by remember { mutableStateOf(false) }
            val selectedCategory = filteredCategories.find { it.id == uiState.selectedCategoryId }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = {
                        selectedCategory?.let {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(parseColor(it.colorHex)))
                        } ?: Icon(Icons.Default.Category, contentDescription = null)
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredCategories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(parseColor(category.colorHex)))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(category.name)
                                }
                            },
                            onClick = {
                                viewModel.onCategorySelect(category.id)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(getIconForName(category.iconName), contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        )
                    }
                }
            }

            // Description Input
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Receiver Input
            OutlinedTextField(
                value = uiState.receiver,
                onValueChange = viewModel::onReceiverChange,
                label = { Text(if (uiState.isIncome) "Sender" else "Receiver") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(if (uiState.isIncome) Icons.Default.Person else Icons.Default.Payments, contentDescription = null) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = viewModel::saveTransaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState.amount.isNotEmpty() && uiState.selectedCategoryId != null
            ) {
                Text(if (uiState.transactionId == null) "Save Transaction" else "Update Transaction", style = MaterialTheme.typography.titleMedium)
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.onDateChange(it) }
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
    }
}

@Composable
fun TypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) selectedColor else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                getIconForName(category.iconName),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            category.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
