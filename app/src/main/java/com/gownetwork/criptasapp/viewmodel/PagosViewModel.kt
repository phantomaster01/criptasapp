package com.gownetwork.criptasapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gownetwork.criptasapp.network.Repository.PagosRepository
import com.gownetwork.criptasapp.network.Response
import com.gownetwork.criptasapp.network.entities.Evidencia
import com.gownetwork.criptasapp.network.entities.EvidenciaCreate
import com.gownetwork.criptasapp.network.entities.Pago
import com.gownetwork.criptasapp.network.entities.PagoCreate
import kotlinx.coroutines.launch

class PagosViewModel(private val repository: PagosRepository, private val context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _pago = MutableLiveData<Pago?>()
    val pago: LiveData<Pago?> get() = _pago

    private val _evidencia = MutableLiveData<Evidencia?>()
    val evidencia: LiveData<Evidencia?> get() = _evidencia

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _pagos = MutableLiveData<List<Pago>>()
    val pagos: LiveData<List<Pago>> get() = _pagos

    fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        return "Bearer $token"
    }

    fun getId(): String? {
        sharedPreferences.getString("id", null)?.let {
            return it
        }
        return null
    }

    fun realizarPago(pagoCreate: PagoCreate) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = getToken()
                val response: Response<Pago> =  repository.setPago(pagoCreate, token)

                if (!response.HasError) {
                    _pago.value = response.Result
                    _error.value = null
                } else {
                    _error.value = "Error en el pago: ${response.Message}"
                    _pago.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al procesar el pago"
                _pago.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun subirEvidencia(create: EvidenciaCreate) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = getToken()
                val response: Response<Evidencia> = repository.setEvidenciaPago(create, token)

                if (!response.HasError) {
                    _evidencia.value = response.Result
                    _error.value = null
                } else {
                    _error.value = "Error en el pago: ${response.Message}"
                    _evidencia.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al procesar el pago"
                _evidencia.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerPagosCliente(idCripta:String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val id = getId()
                val token = getToken()
                val pagosList = repository.getPagos(id, idCripta, token)

                _pagos.value = pagosList
                _error.value = if (pagosList.isEmpty()) "No hay pagos disponibles" else null
            } catch (e: Exception) {
                _error.value = "Error al obtener los pagos"
                _pagos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
