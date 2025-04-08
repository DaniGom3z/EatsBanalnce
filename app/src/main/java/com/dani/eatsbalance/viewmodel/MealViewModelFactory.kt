package com.dani.eatsbalance.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dani.eatsbalance.model.repository.MealRepository

class MealViewModelFactory(
    private val mealRepository: MealRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MealsViewModel::class.java)) {
            MealsViewModel(mealRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
