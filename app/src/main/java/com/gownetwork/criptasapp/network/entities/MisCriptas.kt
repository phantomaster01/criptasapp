package com.gownetwork.criptasapp.network.entities

import com.google.gson.annotations.SerializedName

data class MisCriptas(
    @SerializedName("Id") val id: String,
    @SerializedName("Cripta") val cripta: String,
    @SerializedName("Seccion") val seccion: String,
    @SerializedName("Zona") val zona: String,
    @SerializedName("Iglesia") val iglesia: String,
    @SerializedName("Lat") val latitud: String,
    @SerializedName("Long") val longitud: String,
    @SerializedName("Fallecidos") val fallecidos: Int,
    @SerializedName("Beneficiarios") val beneficiarios: Int,
    @SerializedName("Visitas") val visitas: Int,
    @SerializedName("FechaCompra") val fechaCompra: String
)
