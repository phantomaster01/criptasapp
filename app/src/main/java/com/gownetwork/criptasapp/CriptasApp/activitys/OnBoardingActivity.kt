package com.gownetwork.criptasapp.CriptasApp.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.gownetwork.criptasapp.CriptasApp.MainActivity
import com.gownetwork.criptasapp.CriptasApp.adapters.OnboardingPagerAdapter
import com.gownetwork.criptasapp.CriptasApp.extensions.navigateToMain
import mx.com.gownetwork.criptas.databinding.ActivityOnboardingBinding

class OnBoardingActivity  : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Conectar ViewPager2 con el TabLayout (Indicador)
        TabLayoutMediator(binding.indicator, binding.viewPager) { _, _ -> }.attach()

        // Acción del botón "Siguiente"
        binding.buttonNext.setOnClickListener {
            if (binding.viewPager.currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem += 1
            } else {
                navigateToMain()
            }
        }

        // Acción del botón "Anterior"
        binding.buttonPrevious.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem -= 1
            }
        }

        // Cambiar el texto del botón "Siguiente" en la última pantalla
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.buttonPrevious.isEnabled = position != 0
                binding.buttonNext.text = if (position == adapter.itemCount - 1) "Finalizar" else "Siguiente"
            }
        })
    }
}