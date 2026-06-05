package com.example.budget_planning.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budget_planning.data.local.dao.BudgetDao
import com.example.budget_planning.data.local.dao.CategoryDao
import com.example.budget_planning.data.local.dao.TransactionDao
import com.example.budget_planning.data.local.entity.BudgetEntity
import com.example.budget_planning.data.local.entity.CategoryEntity
import com.example.budget_planning.data.local.entity.TransactionEntity

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class, BudgetEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default categories
                        db.execSQL("INSERT INTO categories (name, iconName, colorHex, isIncome) VALUES ('Food', 'Restaurant', '#FF5722', 0)")
                        db.execSQL("INSERT INTO categories (name, iconName, colorHex, isIncome) VALUES ('Transport', 'DirectionsBus', '#2196F3', 0)")
                        db.execSQL("INSERT INTO categories (name, iconName, colorHex, isIncome) VALUES ('Shopping', 'ShoppingBag', '#E91E63', 0)")
                        db.execSQL("INSERT INTO categories (name, iconName, colorHex, isIncome) VALUES ('Salary', 'Payments', '#4CAF50', 1)")
                        db.execSQL("INSERT INTO categories (name, iconName, colorHex, isIncome) VALUES ('Gift', 'Redeem', '#9C27B0', 1)")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
