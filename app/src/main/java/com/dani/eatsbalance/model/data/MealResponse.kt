package com.dani.eatsbalance.model.data


data class MealResponse(
    val success: Boolean,
    val message: String,
    val meal: MealRequest?
)
