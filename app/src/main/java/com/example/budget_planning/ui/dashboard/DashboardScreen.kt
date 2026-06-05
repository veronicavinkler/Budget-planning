package com.example.budget_planning.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budget_planning.data.repository.AppTheme
import com.example.budget_planning.ui.categories.parseColor
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onNavigateToBudgeting: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToCategories: () -> Unit,
    currentTheme: AppTheme,
    onThemeToggle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = viewModel::previousMonth) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                        }
                        Text(
                            uiState.currentMonthYear, 
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = viewModel::nextMonth) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            if (currentTheme == AppTheme.LOW_CONTRAST) Icons.Filled.Contrast else Icons.Filled.BrightnessHigh,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = onNavigateToAnalytics) {
                        Icon(Icons.Filled.PieChart, contentDescription = "Analytics")
                    }
                    IconButton(onClick = onNavigateToBudgeting) {
                        Icon(Icons.Filled.Settings, contentDescription = "Budgeting")
                    }
                    IconButton(onClick = onNavigateToCategories) {
                        Icon(Icons.Filled.Category, contentDescription = "Manage Categories")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction", modifier = Modifier.size(36.dp))
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                BudgetOverviewCard(
                    totalBalance = uiState.totalBalance,
                    totalBudget = uiState.totalMonthlyBudget,
                    totalSpent = uiState.totalMonthlySpent,
                    currentTheme = currentTheme
                )
            }
            
            if (currentTheme == AppTheme.HIGH_CONTRAST) {
                item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    BalanceStatCard("Income", uiState.totalIncome, Icons.Filled.ArrowUpward, if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Cyan else Color(0xFF4CAF50), currentTheme)
                    BalanceStatCard("Expenses", uiState.totalExpense, Icons.Filled.ArrowDownward, if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Yellow else Color(0xFFF44336), currentTheme)
                }
            }

            if (currentTheme == AppTheme.HIGH_CONTRAST) {
                item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }
            }

            item {
                BudgetHealthCard(uiState.healthSummary, currentTheme)
            }

            if (currentTheme == AppTheme.HIGH_CONTRAST) {
                item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }
            }

            if (uiState.budgetStatus.isNotEmpty()) {
                item {
                    Text("Budget Health", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                items(uiState.budgetStatus) { budgetWithProgress ->
                    BudgetProgressItem(budgetWithProgress, currentTheme)
                }
            }

            item {
                Text("Recent Activity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            if (currentTheme == AppTheme.HIGH_CONTRAST) {
                item { HorizontalDivider(color = MaterialTheme.colorScheme.outline) }
            }

            if (uiState.recentTransactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        val noTransactionsColor = if (currentTheme == AppTheme.HIGH_CONTRAST) 
                            MaterialTheme.colorScheme.onBackground 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                        
                        Text(
                            "No transactions for this month", 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = noTransactionsColor
                        )
                    }
                }
            } else {
                items(uiState.recentTransactions) { item ->
                    TransactionItem(
                        item = item,
                        onEdit = { onEditTransaction(item.transaction.id) },
                        onDelete = { viewModel.deleteTransaction(item.transaction) },
                        currentTheme = currentTheme
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetHealthCard(summary: BudgetHealthSummary, currentTheme: AppTheme) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    val greenColor = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Cyan else Color(0xFF4CAF50)
    val redColor = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Magenta else Color(0xFFF44336)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = if (currentTheme == AppTheme.HIGH_CONTRAST) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Budget Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("In the Green", style = MaterialTheme.typography.labelSmall, color = greenColor)
                    Text(currencyFormatter.format(summary.greenAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${summary.underspentCategoriesCount} Categories", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("In the Red", style = MaterialTheme.typography.labelSmall, color = redColor)
                    Text(currencyFormatter.format(summary.redAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${summary.overspentCategoriesCount} Categories", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun BudgetOverviewCard(totalBalance: Double, totalBudget: Double, totalSpent: Double, currentTheme: AppTheme) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    val remainingBudget = (totalBudget - totalSpent).coerceAtLeast(0.0)
    val progress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = if (currentTheme == AppTheme.HIGH_CONTRAST) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                CircularProgressDiagram(progress = progress, currentTheme = currentTheme)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Left", style = MaterialTheme.typography.labelSmall)
                    Text(
                        currencyFormatter.format(remainingBudget),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Balance", style = MaterialTheme.typography.labelMedium)
                Text(
                    currencyFormatter.format(totalBalance),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Monthly Budget: ${currencyFormatter.format(totalBudget)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Spent: ${currencyFormatter.format(totalSpent)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CircularProgressDiagram(progress: Float, currentTheme: AppTheme) {
    val primaryColor = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Yellow else MaterialTheme.colorScheme.primary
    val trackColor = if (currentTheme == AppTheme.HIGH_CONTRAST) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
    val dividerColor = MaterialTheme.colorScheme.background
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Butt)
        )
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceAtMost(1f),
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Butt)
        )

        if (currentTheme == AppTheme.HIGH_CONTRAST) {
            // Draw white dividers every 90 degrees
            for (i in 0..3) {
                val angle = i * 90f - 90f
                val startX = center.x + (size.minDimension / 2 - 12.dp.toPx()) * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val startY = center.y + (size.minDimension / 2 - 12.dp.toPx()) * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                val endX = center.x + (size.minDimension / 2 + 12.dp.toPx()) * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val endY = center.y + (size.minDimension / 2 + 12.dp.toPx()) * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                
                drawLine(
                    color = dividerColor,
                    start = androidx.compose.ui.geometry.Offset(startX, startY),
                    end = androidx.compose.ui.geometry.Offset(endX, endY),
                    strokeWidth = 4.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun BalanceStatCard(label: String, amount: Double, icon: ImageVector, color: Color, currentTheme: AppTheme) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = if (currentTheme == AppTheme.HIGH_CONTRAST) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (currentTheme == AppTheme.HIGH_CONTRAST) color else color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Black else color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall)
                Text(currencyFormatter.format(amount), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun BudgetProgressItem(item: BudgetWithProgress, currentTheme: AppTheme) {
    val progress = if (item.budget.amount > 0) (item.spentAmount / item.budget.amount).toFloat() else 0f
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    val categoryColor = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Cyan else (item.category?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.primary)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = if (currentTheme == AppTheme.HIGH_CONTRAST) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(categoryColor))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.category?.name ?: "Unknown", fontWeight = FontWeight.Bold)
                }
                Text("${currencyFormatter.format(item.spentAmount)} / ${currencyFormatter.format(item.budget.amount)}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp)) {
                LinearProgressIndicator(
                    progress = { progress.coerceAtMost(1f) },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                    color = if (progress > 0.9f) (if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Magenta else Color.Red) else categoryColor,
                    trackColor = if (currentTheme == AppTheme.HIGH_CONTRAST) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface
                )
                
                if (currentTheme == AppTheme.HIGH_CONTRAST) {
                    val dividerLineColor = MaterialTheme.colorScheme.background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val dividerWidth = 2.dp.toPx()
                        drawLine(dividerLineColor, androidx.compose.ui.geometry.Offset(size.width * 0.25f, 0f), androidx.compose.ui.geometry.Offset(size.width * 0.25f, size.height), dividerWidth)
                        drawLine(dividerLineColor, androidx.compose.ui.geometry.Offset(size.width * 0.5f, 0f), androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height), dividerWidth)
                        drawLine(dividerLineColor, androidx.compose.ui.geometry.Offset(size.width * 0.75f, 0f), androidx.compose.ui.geometry.Offset(size.width * 0.75f, size.height), dividerWidth)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    item: TransactionWithCategory,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    currentTheme: AppTheme
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    val color = if (item.transaction.isIncome) (if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Cyan else Color(0xFF4CAF50)) else (if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Magenta else Color(0xFFF44336))
    val categoryColor = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Yellow else (item.category?.let { parseColor(it.colorHex) } ?: MaterialTheme.colorScheme.primary)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentTheme == AppTheme.HIGH_CONTRAST) categoryColor else categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getIconForName(item.category?.iconName),
                    contentDescription = null,
                    tint = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Black else categoryColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.category?.name ?: "Unknown", 
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.transaction.receiver.isNotBlank()) {
                    Text(
                        (if (item.transaction.isIncome) "From: " else "To: ") + item.transaction.receiver,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    item.transaction.description, 
                    style = MaterialTheme.typography.bodySmall, 
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    (if (item.transaction.isIncome) "+" else "-") + currencyFormatter.format(item.transaction.amount),
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun getIconForName(name: String?): ImageVector {
    return when (name) {
        "Restaurant" -> Icons.Filled.Restaurant
        "DirectionsBus" -> Icons.Filled.DirectionsBus
        "ShoppingBag" -> Icons.Filled.ShoppingBag
        "Payments" -> Icons.Filled.Payments
        "Redeem" -> Icons.Filled.Redeem
        else -> Icons.Filled.Category
    }
}
