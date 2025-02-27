package com.gownetwork.criptasapp.CriptasApp.extensions

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gownetwork.criptasapp.CriptasApp.CriptasLoginActivity

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
    val intent = Intent(this, CriptasLoginActivity::class.java)
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