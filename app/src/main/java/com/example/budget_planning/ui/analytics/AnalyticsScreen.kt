package com.example.budget_planning.ui.analytics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budget_planning.data.repository.AppTheme
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit,
    currentTheme: AppTheme
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Spending Analytics", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Remaining",
                            value = currencyFormatter.format(uiState.totalRemainingBudget),
                            icon = Icons.Default.PieChart
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Top Spend",
                            value = uiState.topSpendingCategory?.name ?: "N/A",
                            icon = Icons.Default.PieChart
                        )
                    }
                }

                item {
                    CalendarHeatmap(uiState.calendarHeatmapData, currentTheme)
                }

                item {
                    TimeOfDayAnalysis(uiState.timeOfDayAnalysis, uiState.peakSpendingPeriod, currentTheme)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(modifier: Modifier = Modifier, title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalendarHeatmap(heatmapData: Map<Int, Double>, currentTheme: AppTheme) {
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val maxSpending = heatmapData.values.maxOrNull() ?: 1.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = if (currentTheme == AppTheme.HIGH_CONTRAST) BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Spending Heatmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            val days = (1..daysInMonth).toList()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = days.chunked(7)
                chunks.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        week.forEach { day ->
                            val spending = heatmapData[day] ?: 0.0
                            val alpha = (spending / maxSpending).toFloat().coerceIn(0.1f, 1f)
                            val baseColor = if (currentTheme == AppTheme.HIGH_CONTRAST) Color.Cyan else MaterialTheme.colorScheme.primary
                            
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (spending > 0) baseColor.copy(alpha = alpha) else MaterialTheme.colorScheme.surface)
                                    .then(if (currentTheme == AppTheme.HIGH_CONTRAST) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)) else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(day.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = if (spending > 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                        if (week.size < 7) {
                            repeat(7 - week.size) {
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeOfDayAnalysis(analysis: Map<String, Double>, peakPeriod: String, currentTheme: AppTheme) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = if (currentTheme == AppTheme.HIGH_CONTRAST) BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Time of Day Analysis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            analysis.forEach { (period, amount) ->
                val isPeak = period == peakPeriod
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(period, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal)
                    Text(
                        currencyFormatter.format(amount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isPeak) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (isPeak) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LinearProgressIndicator(
                    progress = { (amount / analysis.values.maxOfOrNull { it.coerceAtLeast(1.0) }!!).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = if (isPeak) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Peak Spending: $peakPeriod",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
