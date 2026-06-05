package com.example.budget_planning.data.repository

import com.example.budget_planning.data.local.dao.BudgetDao
import com.example.budget_planning.data.local.dao.CategoryDao
import com.example.budget_planning.data.local.dao.TransactionDao
import com.example.budget_planning.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepository(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) {
    // Categories
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
        .map { entities -> entities.map { it.toDomain() } }

    fun getCategoriesByType(isIncome: Boolean): Flow<List<Category>> =
        categoryDao.getCategoriesByType(isIncome)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    // Transactions
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
        .map { entities -> entities.map { it.toDomain() } }

    fun getTransactionsByType(isIncome: Boolean): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(isIncome)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)?.toDomain()

    val totalIncome: Flow<Double> = transactionDao.getTotalIncome().map { it ?: 0.0 }
    val totalExpense: Flow<Double> = transactionDao.getTotalExpense().map { it ?: 0.0 }

    // Budgets
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> =
        budgetDao.getBudgetsByMonth(month, year)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }
}
