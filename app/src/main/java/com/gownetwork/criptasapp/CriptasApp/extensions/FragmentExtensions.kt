package com.gownetwork.criptasapp.CriptasApp.extensions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun View.applySystemBarsPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        v.setPadding(0, statusBarHeight, 0, navBarHeight) // Ajusta los márgenes
        insets
    }
}
fun Fragment.setTitle(title:String){
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}