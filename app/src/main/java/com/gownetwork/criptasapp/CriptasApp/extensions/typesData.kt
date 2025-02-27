package com.gownetwork.criptasapp.CriptasApp.extensions

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.Executors

fun String.removeBase64Prefix(): String {
    return this.replace(Regex("^data:image/[^;]+;base64,"), "")
}

fun String.toReadableDate(): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")

        val formatoSalida = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))

        val fecha: Date? = formatoEntrada.parse(this)
        fecha?.let { formatoSalida.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.toReadableDateY(): String {
    return try {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")

        val formatoSalida = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))

        val fecha: Date? = formatoEntrada.parse(this)
        fecha?.let { formatoSalida.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun Double.toPesos() : String?{
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val precioFormateado = formatoMoneda.format(this)
    return precioFormateado
}

fun Bitmap.toBase64Async(callback: (String) -> Unit) {
    val executor = Executors.newSingleThreadExecutor()
    executor.execute {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Reduce calidad al 50%
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        callback(base64String)
    }
}


