package com.gownetwork.ctiptasapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Base URL del API
private const val BASE_URL = "https://gownetwork.icu:444/api/Movil/"

// Configuración de Logging
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

// Configuración de Retrofit
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
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
