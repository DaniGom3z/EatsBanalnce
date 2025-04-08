package com.dani.eatsbalance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dani.eatsbalance.ui.screen.login.LoginScreen
import com.dani.eatsbalance.ui.screen.RegisterScreen
import com.dani.eatsbalance.viewmodel.AuthViewModel
import com.dani.eatsbalance.viewmodel.MealsViewModel
import com.dani.eatsbalance.ui.screen.dashboard.DashboardScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel ,
    mealViewModel: MealsViewModel
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(navController = navController,
                authViewModel = authViewModel)

        }

        composable("dashboard") {
            DashboardScreen(navController = navController,
                mealViewModel = mealViewModel)

        }


    }
}
