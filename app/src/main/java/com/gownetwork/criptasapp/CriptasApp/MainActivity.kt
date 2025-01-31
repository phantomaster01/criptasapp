package com.gownetwork.criptasapp.CriptasApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.gownetwork.ctiptasapp.R
import com.gownetwork.ctiptasapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtener el NavController desde el NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configurar el BottomNavigationView con el NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }
}
