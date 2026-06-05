package com.example.budget_planning.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.budget_planning.data.model.Category
import com.example.budget_planning.data.model.Transaction
import com.example.budget_planning.data.repository.BudgetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TransactionUiState(
    val transactionId: Long? = null,
    val amount: String = "",
    val description: String = "",
    val receiver: String = "",
    val selectedCategoryId: Long? = null,
    val isIncome: Boolean = false,
    val date: Long = System.currentTimeMillis(),
    val time: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val categories: List<Category> = emptyList(),
    val isTransactionSaved: Boolean = false,
    val isTransactionDeleted: Boolean = false
)

class TransactionViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allCategories.collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onReceiverChange(receiver: String) {
        _uiState.update { it.copy(receiver = receiver) }
    }

    fun onCategorySelect(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onTypeChange(isIncome: Boolean) {
        _uiState.update { it.copy(isIncome = isIncome, selectedCategoryId = null) }
    }

    fun onDateChange(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    fun onTimeChange(time: String) {
        _uiState.update { it.copy(time = time) }
    }

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            repository.getTransactionById(id)?.let { transaction ->
                _uiState.update { it.copy(
                    transactionId = transaction.id,
                    amount = transaction.amount.toString(),
                    description = transaction.description,
                    receiver = transaction.receiver,
                    time = transaction.time,
                    selectedCategoryId = transaction.categoryId,
                    isIncome = transaction.isIncome,
                    date = transaction.timestamp
                ) }
            }
        }
    }

    fun saveTransaction() {
        val currentState = _uiState.value
        val amountValue = currentState.amount.toDoubleOrNull() ?: return
        val categoryId = currentState.selectedCategoryId ?: return

        viewModelScope.launch {
            val transaction = Transaction(
                id = currentState.transactionId ?: 0,
                amount = amountValue,
                categoryId = categoryId,
                description = currentState.description,
                receiver = currentState.receiver,
                time = currentState.time,
                timestamp = currentState.date,
                isIncome = currentState.isIncome
            )
            if (currentState.transactionId != null) {
                repository.updateTransaction(transaction)
            } else {
                repository.insertTransaction(transaction)
            }
            _uiState.update { it.copy(isTransactionSaved = true) }
        }
    }

    fun deleteTransaction() {
        val id = _uiState.value.transactionId ?: return
        viewModelScope.launch {
            val transaction = repository.getTransactionById(id) ?: return@launch
            repository.deleteTransaction(transaction)
            _uiState.update { it.copy(isTransactionDeleted = true) }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isTransactionSaved = false, isTransactionDeleted = false) }
    }
}

class TransactionViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
