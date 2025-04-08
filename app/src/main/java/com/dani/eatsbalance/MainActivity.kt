package com.dani.eatsbalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.dani.eatsbalance.model.network.RetrofitClient
import com.dani.eatsbalance.model.repository.AuthRepository
import com.dani.eatsbalance.model.repository.MealRepository
import com.dani.eatsbalance.viewmodel.AuthViewModel
import com.dani.eatsbalance.viewmodel.AuthViewModelFactory
import com.dani.eatsbalance.viewmodel.MealViewModelFactory
import com.dani.eatsbalance.ui.navigation.AppNavHost
import com.dani.eatsbalance.ui.theme.EatsBalanceTheme
import com.dani.eatsbalance.viewmodel.MealsViewModel


class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.apiService))
    }

    private val mealViewModel: MealsViewModel by viewModels {
        MealViewModelFactory(MealRepository(RetrofitClient.apiService))
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatsBalanceTheme {
                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    mealViewModel = mealViewModel
                )

            }
        }
    }
}
