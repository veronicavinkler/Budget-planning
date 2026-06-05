package com.example.budget_planning.ui.budgeting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budget_planning.ui.dashboard.getIconForName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetingScreen(
    viewModel: BudgetingViewModel,
    onNavigateBack: () -> Unit,
    currentTheme: com.example.budget_planning.data.repository.AppTheme
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Set Monthly Budgets", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets.statusBars
            )
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
                contentPadding = PaddingValues(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.categoryBudgets) { item ->
                    BudgetEditItem(
                        categoryName = item.category.name,
                        iconName = item.category.iconName,
                        initialAmount = item.budget?.amount ?: 0.0,
                        onSave = { amount -> viewModel.updateBudget(item.category.id, amount) }
                    )
                    if (currentTheme == com.example.budget_planning.data.repository.AppTheme.HIGH_CONTRAST) {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 16.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetEditItem(
    categoryName: String,
    iconName: String,
    initialAmount: Double,
    onSave: (Double) -> Unit
) {
    var amountText by remember(initialAmount) { mutableStateOf(if (initialAmount > 0) initialAmount.toString() else "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                getIconForName(iconName),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(categoryName, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Limit") },
                    prefix = { Text("€ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { 
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    onSave(amount)
                },
                enabled = amountText.isNotEmpty()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
