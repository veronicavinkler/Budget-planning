package com.example.budget_planning.ui.budgeting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budget_planning.data.model.Budget
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.data.repository.BudgetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class CategoryBudget(
    val category: Category,
    val budget: Budget?
)

data class BudgetingUiState(
    val categoryBudgets: List<CategoryBudget> = emptyList(),
    val isLoading: Boolean = true
)

class BudgetingViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val uiState: StateFlow<BudgetingUiState> = combine(
        repository.getCategoriesByType(isIncome = false),
        repository.getBudgetsByMonth(currentMonth, currentYear)
    ) { categories, budgets ->
        val budgetMap = budgets.associateBy { it.categoryId }
        val categoryBudgets = categories.map { category ->
            CategoryBudget(category, budgetMap[category.id])
        }
        BudgetingUiState(categoryBudgets = categoryBudgets, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetingUiState()
    )

    fun updateBudget(categoryId: Long, amount: Double) {
        viewModelScope.launch {
            val currentState = uiState.value
            val existingBudget = currentState.categoryBudgets.find { it.category.id == categoryId }?.budget
            
            if (existingBudget != null) {
                repository.updateBudget(existingBudget.copy(amount = amount))
            } else {
                repository.insertBudget(
                    Budget(
                        categoryId = categoryId,
                        amount = amount,
                        month = currentMonth,
                        year = currentYear
                    )
                )
            }
        }
    }
}

class BudgetingViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
