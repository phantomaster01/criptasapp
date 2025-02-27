package com.gownetwork.criptasapp.network.Repository

import com.gownetwork.criptasapp.network.ApiService
import com.gownetwork.criptasapp.network.Response
import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import com.gownetwork.criptasapp.network.entities.MisCriptas
import com.gownetwork.criptasapp.network.entities.SolicitudInfo

class CriptasRepository(private val apiService: ApiService) {

    suspend fun getCriptasDisponibles(idIglesia: String): List<CriptasByIglesia> {
        val response = apiService.getCriptasDisponibles(idIglesia)
        if(response.HasError){
            return emptyList()
        }else{
            return response.Result
        }
    }

    suspend fun getMisCriptas(idCliente: String?, token: String?): List<MisCriptas> {
        val response = apiService.getMisCriptas(idCliente, token)
        if(response.HasError){
            return emptyList()
        }else{
            return response.Result
        }
    }

    suspend fun getMisCripta(idCripta: String?, token: String?): Response<MisCriptas> {
        return apiService.getMisCripta(idCripta, token)
    }

    suspend fun enviarSolicitud(token: String?, solicitud: SolicitudInfo): Response<SolicitudInfo> {
        val response = apiService.crearSolicitud(token, solicitud)
        return response
    }
}