package com.gownetwork.criptasapp.CriptasApp.activitys

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.com.gownetwork.criptas.databinding.ActivityMenuCriptaBinding

class MenuCriptaActivity : AppCompatActivity() {
    lateinit var binding : ActivityMenuCriptaBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var IdCripta: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuCriptaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadId()
    }
    private fun loadId(){
        sharedPreferences = getSharedPreferences("criptas_prefs", Context.MODE_PRIVATE)
        IdCripta = intent.getStringExtra("IdCripta") ?: sharedPreferences.getString("IdCripta", null)
        if (IdCripta == null) {
            Toast.makeText(this, "No se encontró el ID de la cripta", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        sharedPreferences.edit().putString("IdCripta", IdCripta).apply()
    }
}