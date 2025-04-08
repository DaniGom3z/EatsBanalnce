package com.dani.eatsbalance.model.data

data class MealRequest(
    val id: Int = 0,
    val name: String,
    val calories: Int,
    val description: String,
    val date: Long
)
