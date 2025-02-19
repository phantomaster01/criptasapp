package com.gownetwork.criptasapp.CriptasApp.extensions


import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun obtenerFcmToken(): String? {
    return suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Error al obtener el Token", task.exception)
                    continuation.resumeWithException(task.exception ?: Exception("Token no obtenido"))
                } else {
                    val token = task.result
                    Log.d("FCM Token", token ?: "NULO")
                    continuation.resume(token)
                }
            }
    }
}
