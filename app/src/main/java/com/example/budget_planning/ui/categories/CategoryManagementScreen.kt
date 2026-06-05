package com.example.budget_planning.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.ui.dashboard.getIconForName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: CategoryManagementViewModel,
    onNavigateBack: () -> Unit,
    currentTheme: com.example.budget_planning.data.repository.AppTheme
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category", modifier = Modifier.size(36.dp))
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Expense Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(uiState.categories.filter { !it.isIncome }) { category ->
                    CategoryListItem(category, onDelete = { viewModel.deleteCategory(category) })
                    if (currentTheme == com.example.budget_planning.data.repository.AppTheme.HIGH_CONTRAST) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Income Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(uiState.categories.filter { it.isIncome }) { category ->
                    CategoryListItem(category, onDelete = { viewModel.deleteCategory(category) })
                    if (currentTheme == com.example.budget_planning.data.repository.AppTheme.HIGH_CONTRAST) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }

        if (showAddDialog) {
            AddCategoryDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, icon, color, isIncome ->
                    viewModel.addCategory(name, icon, color, isIncome)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun CategoryListItem(category: Category, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(parseColor(category.colorHex).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getIconForName(category.iconName),
                    contentDescription = null,
                    tint = parseColor(category.colorHex)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(category.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf("Category") }
    var selectedColor by remember { mutableStateOf("#FF5722") }

    val icons = listOf(
        "Restaurant", "DirectionsBus", "ShoppingBag", "Payments", "Redeem",
        "Home", "FlashOn", "Movie", "School", "HealthAndSafety", "Category"
    )
    val colors = listOf(
        "#FF5722", "#2196F3", "#E91E63", "#4CAF50", "#9C27B0",
        "#FFC107", "#00BCD4", "#795548", "#607D8B", "#000000"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isIncome, onCheckedChange = { isIncome = it })
                    Text("Is Income Category")
                }

                Text("Icon", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(icons) { iconName ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (selectedIcon == iconName) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedIcon = iconName }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(getIconForName(iconName), contentDescription = null)
                        }
                    }
                }

                Text("Color", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colors) { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(parseColor(colorHex))
                                .clickable { selectedColor = colorHex }
                                .padding(4.dp)
                        ) {
                            if (selectedColor == colorHex) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedIcon, selectedColor, isIncome) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Transparent // Fallback to transparent if invalid, or let theme handle it
    }
}
