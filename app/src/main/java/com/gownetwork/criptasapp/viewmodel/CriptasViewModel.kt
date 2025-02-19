package com.gownetwork.criptasapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gownetwork.criptasapp.network.Repository.CriptasRepository
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import com.gownetwork.criptasapp.network.entities.MisCriptas
import com.gownetwork.criptasapp.network.entities.SolicitudInfo
import kotlinx.coroutines.launch

class CriptasViewModel(private val repository: CriptasRepository, private val context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _criptasDisponibles = MutableLiveData<List<CriptasByIglesia>>()
    val criptasDisponibles: LiveData<List<CriptasByIglesia>> get() = _criptasDisponibles

    private val _misCriptas = MutableLiveData<List<MisCriptas>>()
    val misCriptas: LiveData<List<MisCriptas>> get() = _misCriptas

    private val _solicitudEnviada = MutableLiveData<Boolean>()
    val solicitudEnviada: LiveData<Boolean> get() = _solicitudEnviada


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        return "Bearer $token"
    }

    private fun getId(): String {
        sharedPreferences.getString("id", null)?.let {
            return it
        }
        return ""
    }

    fun fetchCriptasDisponibles(idIglesia: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getCriptasDisponibles(idIglesia, getToken())
                _criptasDisponibles.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar criptas disponibles"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMisCriptas() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getMisCriptas(getId(), getToken())
                _misCriptas.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar mis criptas"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun enviarSolicitud(idServicio: String, mensaje: String) {
        viewModelScope.launch {
            try {
                val solicitud = SolicitudInfo(
                    idCliente = getId(),
                    idServicio = idServicio,
                    mensaje = mensaje
                )
                val response = repository.enviarSolicitud(getToken(), solicitud)
                if (!response.HasError) {
                    _solicitudEnviada.value = true
                } else {
                    _solicitudEnviada.value = false
                    _error.value = response.Message
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            }
        }
    }
}