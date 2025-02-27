package com.gownetwork.criptasapp.network.Repository

import com.gownetwork.criptasapp.network.ApiService
import com.gownetwork.criptasapp.network.Response
import com.gownetwork.criptasapp.network.entities.Evidencia
import com.gownetwork.criptasapp.network.entities.EvidenciaCreate
import com.gownetwork.criptasapp.network.entities.Pago
import com.gownetwork.criptasapp.network.entities.PagoCreate

class PagosRepository (private val apiService: ApiService) {

    suspend fun setPago(pagoCreate: PagoCreate, token: String?): Response<Pago> {
        var response = apiService.crearPago(token,pagoCreate)
        return response
    }

    suspend fun setEvidenciaPago(create: EvidenciaCreate, token: String?): Response<Evidencia> {
        var response = apiService.crearEvidenciaPago(token,create)
        return response
    }

    suspend fun getPagos(id: String?, idCripta: String?, token: String?): List<Pago> {
        var response = apiService.pagosCliente(id,idCripta,token)
        if(response.HasError){
            return emptyList()
        }else{
            return response.Result
        }
    }
}