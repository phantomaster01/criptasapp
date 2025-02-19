package com.gownetwork.criptasapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gownetwork.criptasapp.network.Repository.ServiciosRepository
import com.gownetwork.criptasapp.network.entities.Servicio
import kotlinx.coroutines.launch

class ServiciosViewModel(private val repository: ServiciosRepository, private val context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _servicios = MutableLiveData<List<Servicio>>()
    val servicios: LiveData<List<Servicio>> get() = _servicios

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _servicioDetalle = MutableLiveData<Servicio?>()
    val servicioDetalle: LiveData<Servicio?> get() = _servicioDetalle

    fun getToken(): String? {
        val token =  sharedPreferences.getString("auth_token", null)
        return "Bearer $token";
    }

    fun fetchServicioDetalle(idServicio: String) {
        viewModelScope.launch {
            try {
                val response = repository.getServicioDetalle(idServicio, getToken())
                if (response.HttpCode == 200) {
                    _servicioDetalle.value = response.Result
                } else {
                    _servicioDetalle.value = null
                }
            } catch (e: Exception) {
                _servicioDetalle.value = null
            }
        }
    }

    fun fetchServicios(idIglesia: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getServicios(idIglesia, getToken())
                _servicios.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar servicios"
            } finally {
                _isLoading.value = false
            }
        }
    }
}