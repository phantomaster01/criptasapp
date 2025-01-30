package com.gownetwork.ctiptasapp.CriptasApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gownetwork.ctiptasapp.databinding.ActivityCriptasRegisterBinding

class CriptasRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriptasRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriptasRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Evento de botón para regresar al login
        binding.btnBackToLogin.setOnClickListener {
            finish() // Cierra la actividad y regresa al login
        }
    }
}
