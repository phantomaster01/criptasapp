package com.gownetwork.criptasapp.network.entities

data class Pago (
    var Id: String?,
    var IdCliente : String?,
    var IdCripta : String?,
    val varIdTipoPago: String?,
    var TipoPago: String?,
    var MontoTotal: Double?,
    var FechaLimite: String?,
    var Pagado: Boolean?,
    var FechaRegistro: String?,
    var FechaActualizacion: String?,
    var Estatus: Boolean?,
    var MontoPagado: Double?,
    var FechaPagado: String?,
)