package com.gownetwork.ctiptasapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

private const val BASE_URL = "https://gownetwork.icu:444/api/Movil/"

// 🔹 Configurar un TrustManager que acepte todos los certificados (Solo para pruebas)
private fun getUnsafeOkHttpClient(): OkHttpClient {
    return try {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true } // 🔹 Acepta cualquier host sin verificar
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

// 🔹 Usar Retrofit con la configuración de seguridad modificada
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(getUnsafeOkHttpClient()) // 🔹 Se usa la versión insegura de OkHttpClient
    .build()

// Modelos de datos
data class LoginRequest(val Correo: String, val Password: String)

data class LoginResult(
    val token: String,
    val Id: String
)

data class RegisterRequest(
    val Nombres: String,
    val Apellidos: String,
    val Direccion: String,
    val Telefono: String,
    val Contra: String,
    val Email: String,
    val FechaNac: String,
    val Sexo: String
)

data class Response<T>(
    val HttpCode: Int,
    val HasError: Boolean,
    val Message: String,
    val Result: T
)

// Definir la API
interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResult?>

    @Headers("Content-Type: application/json-patch+json")
    @POST("nuevo")
    suspend fun register(@Body request: RegisterRequest): Response<Boolean>
}

// Instancia única de la API
object ApiClient {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
