package com.gownetwork.criptasapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gownetwork.criptasapp.CriptasApp.extensions.obtenerFcmToken
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.network.LoginRequest
import com.gownetwork.criptasapp.network.RegisterRequest
import com.gownetwork.criptasapp.network.Response
import com.gownetwork.criptasapp.network.entities.UserProfileResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

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

    private val _userProfile = MutableLiveData<UserProfileResponse>()
    val userProfile: LiveData<UserProfileResponse> get() = _userProfile

    private val _profileError = MutableLiveData<String>()
    val profileError: LiveData<String> get() = _profileError

    public fun changeLoanding(valor:Boolean){
        _isLoading.postValue(valor)
    }

    fun fetchUserProfile() {
        val userId = getId()
        val token = getToken()

        if (userId.isNullOrEmpty() || token.isNullOrEmpty()) {
            _profileError.postValue("No se encontró información de usuario.")
            return
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.service.getUserProfile(userId, "Bearer $token")
                if (!response.HasError) {
                    _userProfile.postValue(response.Result)
                } else {
                    _profileError.postValue(response.Message)
                }
            } catch (e: HttpException) {
                _profileError.postValue("Error en la consulta: ${e.message()}")
            } catch (e: Exception) {
                _profileError.postValue("Error de conexión: ${e.message}")
            }
        }
    }

    fun register(
        nombres: String,
        apellidos: String,
        direccion: String,
        telefono: String,
        contra: String,
        email: String,
        fechaNac: String,
        sexo: String
    ) {
        viewModelScope.launch {
            _isLoading.postValue(true)  // Muestra el loader
            delay(2000) // Simula tiempo de carga

            try {
                val response = ApiClient.service.register(
                    RegisterRequest(nombres, apellidos, direccion, telefono, contra, email, fechaNac, sexo)
                )
                if (!response.HasError) {
                    _registerSuccess.postValue(true)
                } else {
                    _registerMessage.postValue(response.Message)
                }
            } catch (e: Exception) {
                _registerMessage.postValue("Error de conexión: ${e.message}")
            }

            _isLoading.postValue(false) // Oculta el loader
        }
    }

    fun login(correo: String, password: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)  // Muestra el loader
            delay(2000) // Simula tiempo de carga

            try {
                val response = ApiClient.service.login(LoginRequest(correo, password, obtenerFcmToken()))
                if (!response.HasError) {
                    saveToken(response.Result?.Token ?: "", response.Result?.Id ?: "")
                    _loginSuccess.postValue(true)
                } else {
                    _loginMessage.postValue(response.Message)
                }
            }catch (e: retrofit2.HttpException) {
                // 🔹 Capturar errores HTTP como 500, 404, etc.
                val errorBody = e.response()?.errorBody()?.string()
                val gson = com.google.gson.Gson()
                val errorResponse = gson.fromJson(errorBody, Response::class.java)
                _loginMessage.postValue(errorResponse.Message)
            } catch (e: Exception) {
                // 🔹 Capturar errores de conexión, tiempo de espera, SSL, etc.
                _loginMessage.postValue("Error de conexión o servidor: ${e.message}")
            }

            _isLoading.postValue(false) // Oculta el loader
        }
    }

    private fun saveToken(token: String, id: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .putString("id", id)
            .apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun getId(): String? {
        return sharedPreferences.getString("id", null)
    }

    fun logout() {
        sharedPreferences.edit()
            .remove("id")
            .remove("auth_token").apply()
    }
}
