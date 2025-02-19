package com.gownetwork.criptasapp.network.entities

import com.google.gson.annotations.SerializedName

data class CriptasByIglesia(
    @SerializedName("Id") val id: String,
    @SerializedName("Cripta") val cripta: String,
    @SerializedName("Seccion") val seccion: String,
    @SerializedName("Zona") val zona: String,
    @SerializedName("Iglesia") val iglesia: String,
    @SerializedName("Lat") val lat: String,
    @SerializedName("Long") val long: String,
    @SerializedName("Estatus") val estatus: Boolean,
    @SerializedName("Disponible") val disponible: Boolean
)

