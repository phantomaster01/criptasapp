package com.gownetwork.criptasapp.CriptasApp.extensions

fun String.removeBase64Prefix(): String {
    return this.replace(Regex("^data:image/[^;]+;base64,"), "")
}
