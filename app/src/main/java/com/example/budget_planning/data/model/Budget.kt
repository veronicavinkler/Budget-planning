package com.example.budget_planning.data.model

import com.example.budget_planning.data.local.entity.BudgetEntity

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val month: Int,
    val year: Int
)

fun BudgetEntity.toDomain() = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year
)

fun Budget.toEntity() = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year
)
