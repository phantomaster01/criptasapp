package com.gownetwork.criptasapp.network.Repository

import com.gownetwork.criptasapp.network.ApiService
import com.gownetwork.criptasapp.network.Response
import com.gownetwork.criptasapp.network.entities.Servicio

class ServiciosRepository(private val apiService: ApiService) {
    suspend fun getServicios(idIglesia: String): List<Servicio> {
        var response = apiService.getServicios(idIglesia)
        if(response.HasError){
            return emptyList()
        }else{
            return response.Result
        }
    }

    suspend fun getServicioDetalle(idServicio: String): Response<Servicio> {
        return apiService.getServicioDetalle(idServicio)
    }

}