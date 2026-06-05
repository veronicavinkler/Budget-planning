package com.example.budget_planning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val description: String,
    val receiver: String = "",
    val time: String = "",
    val timestamp: Long,
    val isIncome: Boolean
)
