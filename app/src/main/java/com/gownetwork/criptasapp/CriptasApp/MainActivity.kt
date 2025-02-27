package com.gownetwork.criptasapp.CriptasApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.gownetwork.criptasapp.CriptasApp.bottomSheet.ServicioBottomSheet
import mx.com.gownetwork.criptas.R
import mx.com.gownetwork.criptas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarX)
        // Obtener el NavController desde el NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configurar el BottomNavigationView con el NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        try{
            val idServicio = intent.getStringExtra("ID_SERVICIO")
            idServicio?.let{
                if (!it.isNullOrEmpty() && idServicio != "0") {
                    onVerMasClick(it)
                }
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private fun onVerMasClick(idServicio: String) {
        val bottomSheet = ServicioBottomSheet(idServicio)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }
}
