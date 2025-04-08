package com.dani.eatsbalance.model.data

data class MealRequest(
    val id: Int = 0,
    val name: String,
    val calories: Int,
    val description: String,
    val date: Long,
    val nutritionRating: Int = 0,   // Calificación de nutrición (0-5)
    val imagePath: String? = null,  // Ruta de la imagen capturada
    val audioPath: String? = null   // Ruta del audio grabado
)