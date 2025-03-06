package com.gownetwork.criptasapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.gownetwork.criptasapp.CriptasApp.CriptasLoginActivity
import com.gownetwork.criptasapp.CriptasApp.MainActivity
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToOnBoarding
import com.gownetwork.criptasapp.CriptasApp.extensions.setupFullScreen
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.viewmodel.AuthViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.com.gownetwork.criptas.databinding.ActivityCriptaAppBinding
import retrofit2.HttpException

class CriptasAppActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    lateinit var binding : ActivityCriptaAppBinding
    var idServicio : String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFullScreen(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        binding = ActivityCriptaAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializar AuthViewModel
        authViewModel = AuthViewModel(this@CriptasAppActivity)

        val deepLinkData = intent?.data
        deepLinkData?.lastPathSegment?.let {
            idServicio = it
        }

        // Verificar si hay token e ID
        val token = authViewModel.getToken()
        val userId = authViewModel.getId()

        if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
            // Si no hay credenciales, enviar a Login
            navigateToOnBoarding(idServicio)
        } else {
            // Si hay credenciales, validar el token con el servidor
            validarToken(userId, token)
        }
    }

    private fun validarToken(userId: String, token: String) {
        lifecycleScope.launch {
            try {
                withContext(NonCancellable) { // Evita la cancelación si la Activity se cierra
                    val response = ApiClient.service.getUserProfile(userId, "Bearer $token")

                    if (response.HttpCode == 200) {
                        navigateToOnBoarding(idServicio)
                    } else {
                        handleInvalidToken()
                    }
                }
            } catch (e: HttpException) {
                Log.e("CriptaAppActivity", "Error HTTP: ${e.response()?.code()}")
                handleInvalidToken()
            } catch (e: Exception) {
                Log.e("CriptaAppActivity", "Error de conexión: ${e.message}")
                handleInvalidToken()
            }
        }
    }

    private fun handleInvalidToken() {
        // Eliminar token e ID almacenado
        authViewModel.logout()
        navigateToOnBoarding(idServicio)
    }
}
