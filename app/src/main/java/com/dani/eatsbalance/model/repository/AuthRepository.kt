package com.dani.eatsbalance.model.repository

import com.dani.eatsbalance.model.data.LoginRequest
import com.dani.eatsbalance.model.data.LoginResponse
import com.dani.eatsbalance.model.network.ApiService
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    // Función para realizar el login
    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return apiService.login(loginRequest)
    }

    // Función para registrar un nuevo usuario
    suspend fun register(registerRequest: LoginRequest): Response<LoginResponse> {
        return apiService.register(registerRequest)
    }
}
