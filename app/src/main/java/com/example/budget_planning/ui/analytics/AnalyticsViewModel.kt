package com.example.budget_planning.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.data.repository.BudgetRepository
import com.example.budget_planning.ui.dashboard.BudgetWithProgress
import kotlinx.coroutines.flow.*
import java.util.*

data class AnalyticsUiState(
    val budgetStatus: List<BudgetWithProgress> = emptyList(),
    val topSpendingCategory: Category? = null,
    val totalRemainingBudget: Double = 0.0,
    val calendarHeatmapData: Map<Int, Double> = emptyMap(),
    val timeOfDayAnalysis: Map<String, Double> = emptyMap(),
    val peakSpendingPeriod: String = "",
    val isLoading: Boolean = true
)

class AnalyticsViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val uiState: StateFlow<AnalyticsUiState> = combine(
        repository.allTransactions,
        repository.allCategories,
        repository.getBudgetsByMonth(currentMonth, currentYear)
    ) { transactions, categories, budgets ->
        val categoryMap = categories.associateBy { it.id }
        
        val filteredTransactions = transactions.filter { isWithinCurrentMonth(it.timestamp) && !it.isIncome }

        val budgetStatus = budgets.map { budget ->
            val spent = filteredTransactions.filter { it.categoryId == budget.categoryId }.sumOf { it.amount }
            BudgetWithProgress(budget, categoryMap[budget.categoryId], spent)
        }

        val topSpending = budgetStatus.maxByOrNull { it.spentAmount }?.category
        val totalBudgeted = budgets.sumOf { it.amount }
        val totalSpent = budgetStatus.sumOf { it.spentAmount }

        // Heatmap Data
        val heatmapData = filteredTransactions.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            cal.get(Calendar.DAY_OF_MONTH)
        }.mapValues { entry -> entry.value.sumOf { it.amount } }

        // Time of Day Analysis
        val timeOfDayMap = mutableMapOf("Morning" to 0.0, "Afternoon" to 0.0, "Evening/Night" to 0.0)
        filteredTransactions.forEach { transaction ->
            val hour = transaction.time.split(":").firstOrNull()?.toIntOrNull() ?: 12
            when (hour) {
                in 6..11 -> timeOfDayMap["Morning"] = timeOfDayMap["Morning"]!! + transaction.amount
                in 12..17 -> timeOfDayMap["Afternoon"] = timeOfDayMap["Afternoon"]!! + transaction.amount
                else -> timeOfDayMap["Evening/Night"] = timeOfDayMap["Evening/Night"]!! + transaction.amount
            }
        }

        val peakPeriod = timeOfDayMap.maxByOrNull { it.value }?.key ?: "N/A"

        AnalyticsUiState(
            budgetStatus = budgetStatus,
            topSpendingCategory = topSpending,
            totalRemainingBudget = (totalBudgeted - totalSpent).coerceAtLeast(0.0),
            calendarHeatmapData = heatmapData,
            timeOfDayAnalysis = timeOfDayMap,
            peakSpendingPeriod = peakPeriod,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    private fun isWithinCurrentMonth(timestamp: Long): Boolean {
        val transCalendar = Calendar.getInstance()
        transCalendar.timeInMillis = timestamp
        return transCalendar.get(Calendar.MONTH) + 1 == currentMonth &&
               transCalendar.get(Calendar.YEAR) == currentYear
    }
}

class AnalyticsViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
