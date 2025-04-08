package com.dani.eatsbalance.model.repository

import com.dani.eatsbalance.model.data.MealRequest
import com.dani.eatsbalance.model.data.MealResponse
import com.dani.eatsbalance.model.network.ApiService
import retrofit2.Response

class MealRepository(private val apiService: ApiService) {

    suspend fun getMeals(): Response<List<MealRequest>> {
        return apiService.getMeals()
    }

    suspend fun getMealById(id: Int): Response<MealRequest> {
        return apiService.getMeal(id)
    }

    suspend fun addMeal(product: MealRequest): Response<MealResponse> {
        return apiService.addMeal(product)
    }

    suspend fun deleteMeal(id: Int): Response<MealResponse> {
        return apiService.deleteMeal(id)
    }
}
