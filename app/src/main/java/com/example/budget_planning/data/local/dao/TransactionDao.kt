package com.example.budget_planning.data.local.dao

import androidx.room.*
import com.example.budget_planning.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isIncome = :isIncome ORDER BY timestamp DESC")
    fun getTransactionsByType(isIncome: Boolean): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT SUM(amount) FROM transactions WHERE isIncome = 1")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isIncome = 0")
    fun getTotalExpense(): Flow<Double?>
}
