package com.gownetwork.criptasapp.network.entities

import java.text.SimpleDateFormat
import java.util.Locale

data class UserProfileResponse(
    val Id: String,
    val Nombres: String,
    val Apellidos: String,
    val Direccion: String,
    val FechaNac: String,
    val Sexo: String,
    val Edad: Int,
    val Telefono: String,
    val Email: String,
    val FechaRegistro: String,
    val FechaActualizacion: String,
    val Estatus: Boolean
)

fun formatFecha(fechaOriginal: String): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = formatoEntrada.parse(fechaOriginal)
        fecha?.let { formatoSalida.format(it) } ?: fechaOriginal
    } catch (e: Exception) {
        fechaOriginal // En caso de error, devuelve la fecha original
    }
}