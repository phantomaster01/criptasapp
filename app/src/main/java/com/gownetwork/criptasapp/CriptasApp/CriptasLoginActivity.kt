package com.gownetwork.criptasapp.CriptasApp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.gownetwork.criptasapp.CriptasApp.bottomSheet.ServicioBottomSheet
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToMain
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToRegister
import com.gownetwork.criptasapp.CriptasApp.extensions.setupFullScreen
import com.gownetwork.criptasapp.viewmodel.AuthViewModel
import com.gownetwork.criptasapp.viewmodel.AuthViewModelFactory
import mx.com.gownetwork.criptas.databinding.ActivityCriptasLoginBinding

class CriptasLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriptasLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setupFullScreen(true)
        binding = ActivityCriptasLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(applicationContext))[AuthViewModel::class.java]

        try{
            val idServicio = intent.getStringExtra("ID_SERVICIO")
            if (!idServicio.isNullOrEmpty() && idServicio != "0") {
                onVerMasClick(idServicio)
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }

        // Observa el estado del login
        authViewModel.loginSuccess.observe(this) { success ->
            if (success) {
                navigateToMain()
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressIndicator.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.GONE
            } else {
                binding.progressIndicator.visibility = View.GONE
                binding.btnLogin.visibility = View.VISIBLE
            }
        }

        authViewModel.loginMessage.observe(this) { message ->
            Toast.makeText(this,message, Toast.LENGTH_LONG).show()
        }

        // Evento de click en el botón de login
        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            authViewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            navigateToRegister()
        }
        binding.btnInvitado.setOnClickListener{
            navigateToMain()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }


    private fun onVerMasClick(idServicio: String) {
        val bottomSheet = ServicioBottomSheet(idServicio)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }
}
