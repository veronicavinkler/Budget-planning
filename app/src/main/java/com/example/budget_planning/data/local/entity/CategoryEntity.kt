package com.example.budget_planning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String, // Material Icon name
    val colorHex: String,
    val isIncome: Boolean = false
)
