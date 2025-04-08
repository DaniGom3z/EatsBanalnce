package com.dani.eatsbalance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dani.eatsbalance.model.repository.AuthRepository
import com.dani.eatsbalance.model.data.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    // Función de login
    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        Log.d("AuthViewModel", "Realizando petición de login para: $email")

        // Realizar la llamada al repositorio
        viewModelScope.launch {
            try {
                val response = authRepository.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _authState.value = AuthState.Success
                        Log.d("AuthViewModel", "Login exitoso: $body")
                    } else {
                        _authState.value = AuthState.Error("Cuerpo vacío en la respuesta")
                        Log.e("AuthViewModel", "Cuerpo vacío en la respuesta")
                    }
                } else {
                    _authState.value = AuthState.Error("Autenticación fallida")
                    Log.e("AuthViewModel", "Error en la respuesta: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error en la conexión")
                Log.e("AuthViewModel", "Error en la conexión: ${e.message}")
            }
        }
    }

    // Función de registro
    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading

        Log.d("AuthViewModel", "Realizando petición de registro para: $email")


        // Realizar la llamada al repositorio
        viewModelScope.launch {
            try {
                val response = authRepository.register(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _authState.value = AuthState.Success
                        Log.d("AuthViewModel", "Registro exitoso: $body")
                    } else {
                        _authState.value = AuthState.Error("Cuerpo vacío en la respuesta")
                        Log.e("AuthViewModel", "Cuerpo vacío en la respuesta")
                    }
                } else {
                    _authState.value = AuthState.Error("Registro fallido")
                    Log.e("AuthViewModel", "Error en la respuesta: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error en la conexión")
                Log.e("AuthViewModel", "Error en la conexión: ${e.message}")
            }
        }
    }
}
