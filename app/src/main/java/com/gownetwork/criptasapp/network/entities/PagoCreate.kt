package com.gownetwork.criptasapp.network.entities

data class PagoCreate (
    var IdCliente : String?,
    var IdCripta : String?,
    var IdTipoPago: String?,
    var TipoPago : Int?,
    var MontoTotal: Double?,
    var FechaLimite: String?
)