package com.dani.eatsbalance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dani.eatsbalance.model.data.MealRequest
import com.dani.eatsbalance.model.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MealState {
    data object Loading : MealState()
    data class Error(val message: String) : MealState()
    data object MealAdded : MealState()
    data object MealDeleted : MealState()
}

class MealsViewModel(private val mealRepository: MealRepository) : ViewModel() {

    private val _meals = MutableStateFlow<List<MealRequest>>(emptyList())
    val meals: StateFlow<List<MealRequest>> = _meals.asStateFlow()

    private val _mealState = MutableStateFlow<MealState?>(null)

    fun fetchMeals() {
        _mealState.value = MealState.Loading
        viewModelScope.launch {
            try {
                val response = mealRepository.getMeals()
                if (response.isSuccessful) {
                    val meals = response.body() ?: emptyList()
                    _meals.value = meals
                    Log.d("MenuViewModel", "Comidas obtenidas: $meals")
                } else {
                    _mealState.value = MealState.Error("Error al obtener comidas")
                }
            } catch (e: Exception) {
                _mealState.value = MealState.Error("Error en la conexión: ${e.message}")
            }
        }
    }

    fun addMeal(meal: MealRequest) {
        _mealState.value = MealState.Loading
        viewModelScope.launch {
            try {
                val response = mealRepository.addMeal(meal)
                if (response.isSuccessful) {
                    _mealState.value = MealState.MealAdded
                    fetchMeals()
                } else {
                    _mealState.value = MealState.Error("Error al agregar cmoida")
                }
            } catch (e: Exception) {
                _mealState.value = MealState.Error("Error en la conexión: ${e.message}")
            }
        }
    }

    fun deleteMeal(mealId: Int) {
        _mealState.value = MealState.Loading
        viewModelScope.launch {
            try {
                val response = mealRepository.deleteMeal(mealId)
                if (response.isSuccessful) {
                    _mealState.value = MealState.MealDeleted
                    fetchMeals()
                } else {
                    _mealState.value = MealState.Error("Error al eliminar producto")
                }
            } catch (e: Exception) {
                _mealState.value = MealState.Error("Error en la conexión: ${e.message}")
            }
        }
    }



}
