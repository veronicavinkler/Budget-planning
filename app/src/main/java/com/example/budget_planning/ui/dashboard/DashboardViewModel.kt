package com.example.budget_planning.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budget_planning.data.model.Budget
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.data.model.Transaction
import com.example.budget_planning.data.repository.BudgetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class DashboardUiState(
    val currentMonthYear: String = "",
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalMonthlyBudget: Double = 0.0,
    val totalMonthlySpent: Double = 0.0,
    val recentTransactions: List<TransactionWithCategory> = emptyList(),
    val budgetStatus: List<BudgetWithProgress> = emptyList(),
    val healthSummary: BudgetHealthSummary = BudgetHealthSummary()
)

data class BudgetHealthSummary(
    val greenAmount: Double = 0.0,
    val redAmount: Double = 0.0,
    val overspentCategoriesCount: Int = 0,
    val underspentCategoriesCount: Int = 0
)

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?
)

data class BudgetWithProgress(
    val budget: Budget,
    val category: Category?,
    val spentAmount: Double
)

class DashboardViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val calendar = MutableStateFlow(Calendar.getInstance())
    private val monthYearFormatter = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = calendar.flatMapLatest { cal ->
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        
        combine(
            repository.allTransactions,
            repository.allCategories,
            repository.getBudgetsByMonth(month, year)
        ) { transactions, categories, budgets ->
            val categoryMap = categories.associateBy { it.id }
            
            val filteredTransactions = transactions.filter { isWithinMonth(it.timestamp, month, year) }
            
            val recentTransactions = filteredTransactions.take(10).map { 
                TransactionWithCategory(it, categoryMap[it.categoryId])
            }

            val budgetStatus = budgets.map { budget ->
                val spent = filteredTransactions.filter { 
                    it.categoryId == budget.categoryId && !it.isIncome
                }.sumOf { it.amount }
                
                BudgetWithProgress(budget, categoryMap[budget.categoryId], spent)
            }

            val totalIncome = filteredTransactions.filter { it.isIncome }.sumOf { it.amount }
            val totalExpense = filteredTransactions.filter { !it.isIncome }.sumOf { it.amount }
            val totalMonthlyBudget = budgets.sumOf { it.amount }
            
            val overspent = budgetStatus.filter { it.spentAmount > it.budget.amount }
            val underspent = budgetStatus.filter { it.spentAmount <= it.budget.amount }

            DashboardUiState(
                currentMonthYear = monthYearFormatter.format(cal.time),
                totalBalance = totalIncome - totalExpense,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                totalMonthlyBudget = totalMonthlyBudget,
                totalMonthlySpent = totalExpense,
                recentTransactions = recentTransactions,
                budgetStatus = budgetStatus,
                healthSummary = BudgetHealthSummary(
                    greenAmount = underspent.sumOf { it.budget.amount - it.spentAmount },
                    redAmount = overspent.sumOf { it.spentAmount - it.budget.amount },
                    overspentCategoriesCount = overspent.size,
                    underspentCategoriesCount = underspent.size
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun nextMonth() {
        calendar.update { 
            val newCal = it.clone() as Calendar
            newCal.add(Calendar.MONTH, 1)
            newCal
        }
    }

    fun previousMonth() {
        calendar.update { 
            val newCal = it.clone() as Calendar
            newCal.add(Calendar.MONTH, -1)
            newCal
        }
    }

    private fun isWithinMonth(timestamp: Long, month: Int, year: Int): Boolean {
        val transCalendar = Calendar.getInstance()
        transCalendar.timeInMillis = timestamp
        return transCalendar.get(Calendar.MONTH) + 1 == month &&
               transCalendar.get(Calendar.YEAR) == year
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}

class DashboardViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
