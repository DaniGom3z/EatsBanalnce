package com.dani.eatsbalance.model.network

import com.dani.eatsbalance.model.data.LoginRequest
import com.dani.eatsbalance.model.data.LoginResponse
import com.dani.eatsbalance.model.data.MealRequest
import com.dani.eatsbalance.model.data.MealResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body registerRequest: LoginRequest): Response<LoginResponse>

    @POST("meal")
    suspend fun addMeal(@Body meal: MealRequest): Response<MealResponse>

    @GET("meal")
    suspend fun getMeals(): Response<List<MealRequest>>

    @GET("meal/{id}")
    suspend fun getMeal(@Path("id") id: Int): Response<MealRequest>

    @DELETE("meal/{id}")
    suspend fun deleteMeal(@Path("id") id: Int): Response<MealResponse>
}
