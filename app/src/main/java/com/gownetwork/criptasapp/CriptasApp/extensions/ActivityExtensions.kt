package com.gownetwork.criptasapp.CriptasApp.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gownetwork.criptasapp.CriptasApp.CriptasLoginActivity
import com.gownetwork.criptasapp.CriptasApp.CriptasRegisterActivity
import com.gownetwork.criptasapp.CriptasApp.MainActivity
import com.gownetwork.criptasapp.CriptasApp.activitys.OnBoardingActivity

fun AppCompatActivity.setupFullScreen(isHide: Boolean) {
    if (isHide) supportActionBar?.hide()

    WindowCompat.setDecorFitsSystemWindows(window, true)

    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightStatusBars = false

    if (Build.VERSION.SDK_INT >= 34) {
        window.isNavigationBarContrastEnforced = false
    }
}

fun Activity.navigateToRegister() {
    val intent = Intent(this, CriptasRegisterActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}

fun Activity.navigateToLogin() {
    val intent = Intent(this, CriptasLoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}

fun Activity.navigateToMain(idServicio:String?=null) {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    idServicio?.let { intent.putExtra("ID_SERVICIO", it) }
    startActivity(intent)
    finish()
}

fun Activity.navigateToOnBoarding(idServicio:String?=null) {
    val sharedPreferences = getSharedPreferences("criptas_prefs", Context.MODE_PRIVATE)
    val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

    if (isFirstTime) {
        // Marcar que el onboarding ya se mostró
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()

        val intent = Intent(this, OnBoardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    } else {
        navigateToMain(idServicio)
    }
}