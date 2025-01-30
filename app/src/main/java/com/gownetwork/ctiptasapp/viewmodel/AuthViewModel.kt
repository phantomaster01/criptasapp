package com.gownetwork.ctiptasapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gownetwork.ctiptasapp.network.ApiClient
import com.gownetwork.ctiptasapp.network.LoginRequest
import com.gownetwork.ctiptasapp.network.RegisterRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String> get() = _loginMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> get() = _registerSuccess

    private val _registerMessage = MutableLiveData<String>()
    val registerMessage: LiveData<String> get() = _registerMessage
    

    fun login(correo: String, password: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)  // Muestra el loader
            delay(2000) // Simula tiempo de carga

            try {
                val response = ApiClient.service.login(LoginRequest(correo, password))
                if (!response.HasError) {
                    saveToken(response.Result?.token ?: "")
                    _loginSuccess.postValue(true)
                } else {
                    _loginMessage.postValue(response.Message)
                }
            } catch (e: Exception) {
                _loginMessage.postValue("Error de conexión: ${e.message}")
            }

            _isLoading.postValue(false) // Oculta el loader
        }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun logout() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
}
