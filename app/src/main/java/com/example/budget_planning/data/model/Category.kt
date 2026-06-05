package com.example.budget_planning.data.model

import com.example.budget_planning.data.local.entity.CategoryEntity

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isIncome: Boolean
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    isIncome = isIncome
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    isIncome = isIncome
)
