package com.gownetwork.criptasapp.CriptasApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gownetwork.ctiptasapp.databinding.ActivityCriptasDisponiblesBinding

class CriptasDisponiblesActivity : AppCompatActivity() {
    lateinit var binding : ActivityCriptasDisponiblesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityCriptasDisponiblesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}