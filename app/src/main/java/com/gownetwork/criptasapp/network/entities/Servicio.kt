package com.gownetwork.criptasapp.network.entities

data class Servicio(
    val Id: String,
    val Nombre: String,
    val Img: String,
    val Descripcion: String?,
    val Estatus: Boolean?,
    val IdIglesia: String?,
    val FechaRegistro: String?,
    val FechaActualizacion: String?
)

