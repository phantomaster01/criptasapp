package com.gownetwork.criptasapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gownetwork.criptasapp.CriptasApp.CriptasLoginActivity
import com.gownetwork.criptasapp.CriptasApp.MainActivity
import com.gownetwork.criptasapp.network.ApiClient
import com.gownetwork.criptasapp.viewmodel.AuthViewModel
import com.gownetwork.ctiptasapp.databinding.ActivityCriptaAppBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CriptasAppActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    lateinit var binding : ActivityCriptaAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        binding = ActivityCriptaAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializar AuthViewModel
        authViewModel = AuthViewModel(this)

        // Verificar si hay token e ID
        val token = authViewModel.getToken()
        val userId = authViewModel.getId()

        if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
            // Si no hay credenciales, enviar a Login
            navigateToLogin()
        } else {
            // Si hay credenciales, validar el token con el servidor
            validarToken(userId, token)
        }
    }

    private fun validarToken(userId: String, token: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getUserProfile(userId, "Bearer $token")

                if (response.HttpCode == 200) {
                    // Token válido, ir al MainActivity
                    navigateToMain()
                } else {
                    // Token inválido, borrar datos y enviar a Login
                    handleInvalidToken()
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

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra esta actividad para evitar que el usuario regrese aquí
    }

    private fun navigateToLogin() {
        val intent = Intent(this, CriptasLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleInvalidToken() {
        // Eliminar token e ID almacenado
        authViewModel.logout()
        navigateToLogin()
    }
}
