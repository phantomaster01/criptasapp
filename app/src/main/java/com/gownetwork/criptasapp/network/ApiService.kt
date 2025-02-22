package com.gownetwork.criptasapp.network

import com.gownetwork.criptasapp.network.entities.CriptasByIglesia
import com.gownetwork.criptasapp.network.entities.Iglesia
import com.gownetwork.criptasapp.network.entities.MisCriptas
import com.gownetwork.criptasapp.network.entities.Servicio
import com.gownetwork.criptasapp.network.entities.SolicitudInfo
import com.gownetwork.criptasapp.network.entities.UserProfileResponse
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
private const val BASE_URL = "https://gownetwork.dyndns.org"



val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1)) // 🔹 Solo permite HTTP/1.1
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    )
    .build()



// Modelos de datos
data class LoginRequest(
    val Correo: String,
    val Password: String,
    val TokenFireBase:String?
)

data class LoginResult(
    val Token: String,
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

    @POST("/api/Movil/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResult?>

    @Headers("Content-Type: application/json-patch+json")
    @POST("/api/Movil/nuevo")
    suspend fun register(@Body request: RegisterRequest): Response<Boolean>

    @GET("/api/Clientes/{id}")
    suspend fun getUserProfile(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @GET("/api/Iglesias/List")
    suspend fun getIglesias(@Header("Authorization") token: String): Response<List<Iglesia>>

    @GET("/api/servicios/ListActive/{idIglesia}")
    suspend fun getServicios(
        @Path("idIglesia") idIglesia: String,
        @Header("Authorization") token: String?
    ): Response<List<Servicio>>

    @GET("/api/Movil/servicio/{idServicio}")
    suspend fun getServicioDetalle(
        @Path("idServicio") idServicio: String,
        @Header("Authorization") token: String?
    ): Response<Servicio>

    @GET("/api/Criptas/ListDisponible/Iglesia/{id}")
    suspend fun getCriptasDisponibles(
        @Path("id") idIglesia: String,
        @Header("Authorization") token: String?
    ): Response<List<CriptasByIglesia>>

    @GET("/api/Clientes/MisCriptas/{id}")
    suspend fun getMisCriptas(
        @Path("id") idCliente: String,
        @Header("Authorization") token: String?
    ): Response<List<MisCriptas>>

    @POST("/api/SolicitudesInfo/Create")
    suspend fun crearSolicitud(
        @Header("Authorization") token: String?,
        @Body solicitud: SolicitudInfo
    ): Response<SolicitudInfo>

}

object ApiClient {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
