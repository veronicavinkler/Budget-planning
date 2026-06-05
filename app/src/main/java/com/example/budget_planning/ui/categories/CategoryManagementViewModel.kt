package com.example.budget_planning.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.data.repository.BudgetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryManagementUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

class CategoryManagementViewModel(private val repository: BudgetRepository) : ViewModel() {

    val uiState: StateFlow<CategoryManagementUiState> = repository.allCategories
        .map { categories -> CategoryManagementUiState(categories = categories, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryManagementUiState()
        )

    fun addCategory(name: String, iconName: String, colorHex: String, isIncome: Boolean) {
        viewModelScope.launch {
            repository.insertCategory(
                Category(
                    name = name,
                    iconName = iconName,
                    colorHex = colorHex,
                    isIncome = isIncome
                )
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}

class CategoryManagementViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryManagementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
