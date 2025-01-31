package com.gownetwork.criptasapp.CriptasApp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.gownetwork.criptasapp.viewmodel.AuthViewModel
import com.gownetwork.criptasapp.viewmodel.AuthViewModelFactory
import com.gownetwork.ctiptasapp.databinding.ActivityCriptasLoginBinding

class CriptasLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriptasLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityCriptasLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(applicationContext))[AuthViewModel::class.java]

        // Observa el estado del login
        authViewModel.loginSuccess.observe(this) { success ->
            if (success) {
                goToMainActivity()
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
            val intent = Intent(this, CriptasRegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra LoginActivity para evitar volver atrás con el botón de retroceso
    }
}
