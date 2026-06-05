package com.example.budget_planning

import android.app.Application
import com.example.budget_planning.data.local.AppDatabase
import com.example.budget_planning.data.repository.BudgetRepository
import com.example.budget_planning.data.repository.ThemeRepository

class BudgetApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { 
        BudgetRepository(
            database.categoryDao(),
            database.transactionDao(),
            database.budgetDao()
        )
    }
    val themeRepository by lazy { ThemeRepository(this) }
}
