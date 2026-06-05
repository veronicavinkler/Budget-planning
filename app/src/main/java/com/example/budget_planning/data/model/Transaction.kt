package com.example.budget_planning.data.model

import com.example.budget_planning.data.local.entity.TransactionEntity

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val description: String,
    val receiver: String = "",
    val time: String = "",
    val timestamp: Long,
    val isIncome: Boolean
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    categoryId = categoryId,
    description = description,
    receiver = receiver,
    time = time,
    timestamp = timestamp,
    isIncome = isIncome
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    categoryId = categoryId,
    description = description,
    receiver = receiver,
    time = time,
    timestamp = timestamp,
    isIncome = isIncome
)
